"""Merge GTFS stops that belong to the same physical stop.

BKK's feed has a separate stop for every direction, platform, metro/HÉV variant
and entrance. This tool groups them by normalized name and distance, then writes
a simplified stops.txt and a stop_times.txt that references the merged stop ids,
so the app can load them without any remapping at startup.

Usage: python3 tools/merge-stops.py [--gtfs-directory data] [--output-directory data/simplified] [--merge-radius-meters 500]
"""

import argparse
import csv
import math
import re
from collections import Counter, defaultdict
from pathlib import Path

EARTH_RADIUS_METERS = 6_371_000

SQUARE_BRACKET_TAG_PATTERN = re.compile(r"\s*\[[^\]]*\]")
PARENTHESIZED_NOTE_PATTERN = re.compile(r"\s*\([^)]*\)")
METRO_OR_SUBURBAN_RAIL_SUFFIX_PATTERN = re.compile(r"\s+(M\+H|M|H)$")
REPEATED_WHITESPACE_PATTERN = re.compile(r"\s+")


def normalize(stop_name: str) -> str:
    normalized_name = SQUARE_BRACKET_TAG_PATTERN.sub("", stop_name)
    normalized_name = PARENTHESIZED_NOTE_PATTERN.sub("", normalized_name)
    normalized_name = REPEATED_WHITESPACE_PATTERN.sub(" ", normalized_name).strip()
    while True:
        name_without_suffix = METRO_OR_SUBURBAN_RAIL_SUFFIX_PATTERN.sub("", normalized_name)
        if name_without_suffix == normalized_name:
            return normalized_name
        normalized_name = name_without_suffix


def distance(first_stop: dict, second_stop: dict) -> float:
    first_latitude = math.radians(float(first_stop["stop_lat"]))
    first_longitude = math.radians(float(first_stop["stop_lon"]))
    second_latitude = math.radians(float(second_stop["stop_lat"]))
    second_longitude = math.radians(float(second_stop["stop_lon"]))

    east_west_angle = (second_longitude - first_longitude) * math.cos((first_latitude + second_latitude) / 2)
    north_south_angle = second_latitude - first_latitude
    return math.hypot(east_west_angle, north_south_angle) * EARTH_RADIUS_METERS


class UnionFind:
    """Disjoint sets of stop ids, each set is one physical stop."""

    def __init__(self, stop_ids):
        self.parent_by_stop_id = {stop_id: stop_id for stop_id in stop_ids}

    def find_representative(self, stop_id):
        while self.parent_by_stop_id[stop_id] != stop_id:
            grandparent_stop_id = self.parent_by_stop_id[self.parent_by_stop_id[stop_id]]
            self.parent_by_stop_id[stop_id] = grandparent_stop_id
            stop_id = grandparent_stop_id
        return stop_id

    def merge_sets(self, first_stop_id, second_stop_id):
        first_representative = self.find_representative(first_stop_id)
        second_representative = self.find_representative(second_stop_id)
        self.parent_by_stop_id[first_representative] = second_representative


def is_boarding_stop(stop: dict) -> bool:
    return stop.get("location_type", "") in ("", "0")


def group_stops_by_physical_location(stops: list[dict], merge_radius_meters: float) -> list[list[dict]]:
    stops_by_id = {stop["stop_id"]: stop for stop in stops}
    union_find = UnionFind(stops_by_id)

    for stop in stops:
        parent_station_id = stop.get("parent_station", "")
        if parent_station_id in stops_by_id:
            union_find.merge_sets(stop["stop_id"], parent_station_id)

    stops_by_normalized_name = defaultdict(list)
    for stop in stops:
        stops_by_normalized_name[normalize(stop["stop_name"])].append(stop)
    for stops_with_same_name in stops_by_normalized_name.values():
        for first_index, first_stop in enumerate(stops_with_same_name):
            for second_stop in stops_with_same_name[first_index + 1:]:
                if distance(first_stop, second_stop) < merge_radius_meters:
                    union_find.merge_sets(first_stop["stop_id"], second_stop["stop_id"])

    stops_by_representative_id = defaultdict(list)
    for stop in stops:
        stops_by_representative_id[union_find.find_representative(stop["stop_id"])].append(stop)
    return list(stops_by_representative_id.values())


def build_merged_stop_row(stop_group: list[dict], stops_header: list[str]) -> dict:
    station_ids = sorted(stop["stop_id"] for stop in stop_group if stop.get("location_type") == "1")
    merged_stop_id = station_ids[0] if station_ids else min(stop["stop_id"] for stop in stop_group)
    merged_stop_name = Counter(normalize(stop["stop_name"]) for stop in stop_group).most_common(1)[0][0]

    stops_used_for_position = [stop for stop in stop_group if is_boarding_stop(stop)] or stop_group
    average_latitude = sum(float(stop["stop_lat"]) for stop in stops_used_for_position) / len(stops_used_for_position)
    average_longitude = sum(float(stop["stop_lon"]) for stop in stops_used_for_position) / len(stops_used_for_position)

    merged_stop_row = {column_name: "" for column_name in stops_header}
    merged_stop_row.update(
        stop_id=merged_stop_id,
        stop_name=merged_stop_name,
        stop_lat=f"{average_latitude:.6f}",
        stop_lon=f"{average_longitude:.6f}",
        stop_code=merged_stop_id,
    )
    return merged_stop_row


def rewrite_stop_times(
    original_stop_times_path: Path,
    simplified_stop_times_path: Path,
    merged_stop_id_by_original_stop_id: dict[str, str],
) -> tuple[int, int]:
    """Stream stop_times, remapping stop ids and collapsing consecutive repeats of the same merged stop."""
    written_row_count = collapsed_row_count = 0
    with (
        original_stop_times_path.open(encoding="utf-8", newline="") as original_file,
        simplified_stop_times_path.open("w", encoding="utf-8", newline="") as simplified_file,
    ):
        stop_times_reader = csv.DictReader(original_file)
        stop_times_writer = csv.DictWriter(simplified_file, fieldnames=stop_times_reader.fieldnames, lineterminator="\n")
        stop_times_writer.writeheader()

        previous_stop_time = None
        for stop_time in stop_times_reader:
            original_stop_id = stop_time["stop_id"]
            if original_stop_id not in merged_stop_id_by_original_stop_id:
                raise SystemExit(f"stop_times references unknown stop_id: {original_stop_id}")
            stop_time["stop_id"] = merged_stop_id_by_original_stop_id[original_stop_id]

            is_same_trip_at_same_merged_stop = (
                previous_stop_time is not None
                and previous_stop_time["trip_id"] == stop_time["trip_id"]
                and previous_stop_time["stop_id"] == stop_time["stop_id"]
            )
            if is_same_trip_at_same_merged_stop:
                previous_stop_time["departure_time"] = stop_time["departure_time"]
                collapsed_row_count += 1
                continue
            if previous_stop_time is not None:
                stop_times_writer.writerow(previous_stop_time)
                written_row_count += 1
            previous_stop_time = stop_time
        if previous_stop_time is not None:
            stop_times_writer.writerow(previous_stop_time)
            written_row_count += 1
    return written_row_count, collapsed_row_count


def main():
    argument_parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    argument_parser.add_argument("--gtfs-directory", type=Path, default=Path("data"))
    argument_parser.add_argument("--output-directory", type=Path, default=Path("data/simplified"))
    argument_parser.add_argument(
        "--merge-radius-meters", type=float, default=500.0, help="max metres between same-named stops"
    )
    arguments = argument_parser.parse_args()

    with (arguments.gtfs_directory / "stops.txt").open(encoding="utf-8", newline="") as original_stops_file:
        original_stops_reader = csv.DictReader(original_stops_file)
        stops_header = original_stops_reader.fieldnames
        original_stops = list(original_stops_reader)

    stop_groups = group_stops_by_physical_location(original_stops, arguments.merge_radius_meters)
    merged_stop_id_by_original_stop_id = {}
    served_merged_stops = []
    for stop_group in stop_groups:
        merged_stop_row = build_merged_stop_row(stop_group, stops_header)
        for stop in stop_group:
            merged_stop_id_by_original_stop_id[stop["stop_id"]] = merged_stop_row["stop_id"]
        if any(is_boarding_stop(stop) for stop in stop_group):
            served_merged_stops.append((merged_stop_row, stop_group))

    arguments.output_directory.mkdir(parents=True, exist_ok=True)
    with (arguments.output_directory / "stops.txt").open("w", encoding="utf-8", newline="") as simplified_stops_file:
        simplified_stops_writer = csv.DictWriter(simplified_stops_file, fieldnames=stops_header, lineterminator="\n")
        simplified_stops_writer.writeheader()
        for merged_stop_row, _ in sorted(served_merged_stops, key=lambda merged_stop: merged_stop[0]["stop_id"]):
            simplified_stops_writer.writerow(merged_stop_row)

    written_row_count, collapsed_row_count = rewrite_stop_times(
        arguments.gtfs_directory / "stop_times.txt",
        arguments.output_directory / "stop_times.txt",
        merged_stop_id_by_original_stop_id,
    )

    groups_with_multiple_stops_count = sum(1 for _, stop_group in served_merged_stops if len(stop_group) > 1)
    print(f"stops: {len(original_stops)} -> {len(served_merged_stops)} ({groups_with_multiple_stops_count} merged groups)")
    print(f"stop_times: {written_row_count} rows written, {collapsed_row_count} consecutive repeats collapsed")
    print("largest groups:")
    largest_merged_stops = sorted(served_merged_stops, key=lambda merged_stop: -len(merged_stop[1]))[:10]
    for merged_stop_row, stop_group in largest_merged_stops:
        print(f"  {len(stop_group):3d}  {merged_stop_row['stop_id']:10s} {merged_stop_row['stop_name']}")


if __name__ == "__main__":
    main()
