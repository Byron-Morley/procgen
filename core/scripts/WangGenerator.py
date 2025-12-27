#!/usr/bin/python

import xml.etree.ElementTree as ET
import json
from PIL import Image
import os
import glob


def read_reference_file(ref_file_path):
    try:
        with open(ref_file_path, 'r') as file:
            data = json.load(file)
        return data
    except FileNotFoundError:
        print(f"Error: Reference file not found at {ref_file_path}")
        return None
    except json.JSONDecodeError:
        print(f"Error: Invalid JSON format in reference file {ref_file_path}")
        return None


def read_wang_tsx(TSX_FILE):
    try:
        tree = ET.parse(TSX_FILE)
        root = tree.getroot()
        return root.findall('.//wangtile')
    except FileNotFoundError:
        print(f"Error: TSX file not found at {TSX_FILE}")
        return None
    except ET.ParseError:
        print(f"Error: Invalid XML format in TSX file {TSX_FILE}")
        return None


def get_source_images(input_folder):
    tiles_folder = os.path.join(input_folder, "tiles")
    if not os.path.exists(tiles_folder):
        print(f"Error: Tiles folder not found at {tiles_folder}")
        return []

    # Get all image files (png, jpg, jpeg)
    image_files = []
    for ext in ['*.png', '*.jpg', '*.jpeg']:
        image_files.extend(glob.glob(os.path.join(tiles_folder, ext)))

    print(f"Found {len(image_files)} source images in {tiles_folder}")
    return image_files


def get_filename_without_extension(file_path):
    """
    Extract the filename without extension from a path.
    Args:
        file_path (str): Full path to the file
    Returns:
        str: Filename without extension
    """
    base_name = os.path.basename(file_path)
    return os.path.splitext(base_name)[0]


def get_base_name(file_name):
    """
    Extract the base name from a hyphenated filename (e.g., 'summer-default' -> 'summer')
    Args:
        file_name (str): Filename
    Returns:
        str: Base name (part before first hyphen)
    """
    parts = file_name.split('-')
    return parts[0] if len(parts) > 1 else file_name


def process_files():
    # Tile dimensions
    tile_width = 16
    tile_height = 16

    # Output image dimensions
    new_image_width = 1024
    new_image_height = 512

    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    # Get the root directory (two levels up from the script)
    root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))

    # Define paths
    INPUT_FOLDER = os.path.join(root_dir, "assets", "raw", "pre-processed", "terrain")
    TSX_FILE = os.path.join(INPUT_FOLDER, "ref", "wang.tsx")
    REF_FILE = os.path.join(INPUT_FOLDER, "ref", "reference.json")
    INDEXES_FILE = os.path.join(INPUT_FOLDER, "ref", "indexes.json")
    SOURCE_FOLDER = os.path.join(INPUT_FOLDER, "source")

    # Output base paths
    OUTPUT_FOLDER = os.path.join(INPUT_FOLDER, "output")
    TILE_BASE_FOLDER = os.path.join(root_dir, "assets", "raw", "sprites", "16x16", "terrain")

    # Create output directories if they don't exist
    os.makedirs(OUTPUT_FOLDER, exist_ok=True)
    os.makedirs(TILE_BASE_FOLDER, exist_ok=True)
    os.makedirs(SOURCE_FOLDER, exist_ok=True)

    # Read the reference file
    ref = read_reference_file(REF_FILE)
    if not ref:
        print("Failed to read reference file. Exiting.")
        return

    # Read indexes file
    indexes_data = read_reference_file(INDEXES_FILE)
    if not indexes_data:
        print("Failed to read indexes file. Continuing without additional sprites.")

    # Read wang tiles
    wang_tiles = read_wang_tsx(TSX_FILE)
    if not wang_tiles:
        print("Failed to find wang tiles. Exiting.")
        return

    # Get all source images
    source_images = get_source_images(INPUT_FOLDER)
    if not source_images:
        print("No source images found. Exiting.")
        return

    # Process each source image separately
    for source_image_path in source_images:
        print(f"\nProcessing source image: {source_image_path}")

        # Get source file name without extension for naming output files
        source_name = get_filename_without_extension(source_image_path)

        # Get base name (part before first hyphen)
        base_name = get_base_name(source_name)

        # Create specific output paths for this source
        DESTINATION_IMAGE = os.path.join(OUTPUT_FOLDER, f"combined_{source_name}.png")
        TILE_FOLDER = os.path.join(TILE_BASE_FOLDER, source_name)
        FILENAME_PREFIX = "terrain_"

        # Create specific output directory for this source's tiles
        os.makedirs(TILE_FOLDER, exist_ok=True)

        try:
            # Create new image for the combined output for this source
            new_image = Image.new("RGB", (new_image_width, new_image_height), color=(255, 255, 255))

            # Open the source image
            original_image = Image.open(source_image_path)
            original_image_width, original_image_height = original_image.size

            # Track processed tiles for this source
            added = []
            last_index = 1300  # Reset for each source

            # Process wang tiles for this image
            for wang_tile in wang_tiles:
                wang_id = wang_tile.get('wangid')
                tile_id = wang_tile.get('tileid')

                print(f"Processing wang tile: {wang_id}")

                # Convert wang ID to index
                int_array = [int(x) for x in wang_id.split(',')]
                new_index = 0
                for index, value in enumerate(int_array):
                    if value > 1:
                        ref_key = str(value)
                        num = ref[ref_key][index]
                        new_index += num

                adjusted_index = new_index
                print(f"Calculated index: {adjusted_index}")

                # Handle duplicate indices
                if new_index in added:
                    adjusted_index = last_index
                    last_index += 1
                    print(f"Duplicate index found, using: {adjusted_index}")

                # Extract tile from source image
                try:
                    x, y = index_to_coords(int(tile_id), original_image_width, original_image_height, tile_width, tile_height)
                    left, top, right, bottom = get_square_coordinates(x, y, tile_width)
                    sub_image = original_image.crop((left, top, right, bottom))

                    # Add to combined image for this source
                    new_coords = index_to_coords(adjusted_index, new_image_width, new_image_height, tile_width, tile_height)
                    new_image.paste(sub_image, new_coords)

                    # Save individual tile with source-specific name
                    tile_output_path = os.path.join(TILE_FOLDER, f"{FILENAME_PREFIX}{adjusted_index}.png")
                    tile_sprite = Image.new("RGB", (tile_width, tile_height))
                    tile_sprite.paste(sub_image)
                    tile_sprite.save(tile_output_path)

                    added.append(adjusted_index)
                    print(f"Saved tile to: {tile_output_path}")

                except Exception as e:
                    print(f"Error processing tile {tile_id} from {source_image_path}: {e}")
                    continue

            # Save the combined image for this source
            new_image.save(DESTINATION_IMAGE)
            print(f"Saved combined image to: {DESTINATION_IMAGE}")

            # Now process additional sprites from the related image
            if indexes_data:
                # Construct the path to the related image
                related_image_path = os.path.join(SOURCE_FOLDER, f"{base_name}.png")

                if os.path.exists(related_image_path):
                    print(f"\nProcessing additional sprites from: {related_image_path}")

                    try:
                        # Open the related image
                        related_image = Image.open(related_image_path)
                        related_image_width = related_image.width
                        related_image_height = related_image.height

                        # Process each index from the indexes file
                        for index in indexes_data.get("indexes", []):
                            try:
                                # Extract the tile from the related image
                                x, y = index_to_coords(index, related_image_width, related_image_height, tile_width, tile_height)
                                left, top, right, bottom = get_square_coordinates(x, y, tile_width)
                                sub_image = related_image.crop((left, top, right, bottom))

                                # Add to the combined image
                                new_coords = index_to_coords(last_index, new_image_width, new_image_height, tile_width, tile_height)
                                new_image.paste(sub_image, new_coords)

                                # Save individual tile
                                tile_output_path = os.path.join(TILE_FOLDER, f"{FILENAME_PREFIX}{last_index}.png")
                                tile_sprite = Image.new("RGB", (tile_width, tile_height))
                                tile_sprite.paste(sub_image)
                                tile_sprite.save(tile_output_path)

                                print(f"Added additional sprite at index {index} as {last_index}")
                                last_index += 1

                            except Exception as e:
                                print(f"Error processing additional sprite at index {index}: {e}")
                                continue

                        # Save the updated combined image
                        new_image.save(DESTINATION_IMAGE)
                        print(f"Updated combined image with additional sprites: {DESTINATION_IMAGE}")

                    except Exception as e:
                        print(f"Error opening related image {related_image_path}: {e}")
                else:
                    print(f"Related image not found: {related_image_path}")

        except Exception as e:
            print(f"Error processing source image {source_image_path}: {e}")
            continue


def index_to_coords(index, image_width, image_height, tile_width, tile_height):
    """
    Convert a tile index to x,y coordinates in the image.
    Args:
        index (int): Tile index
        image_width (int): Width of the image
        image_height (int): Height of the image
        tile_width (int): Width of each tile
        tile_height (int): Height of each tile
    Returns:
        tuple: (x, y) coordinates
    """
    cells_per_row = image_width // tile_width
    row_index = index // cells_per_row
    column_index = index % cells_per_row
    x = column_index * tile_width
    y = row_index * tile_height
    return x, y


def get_square_coordinates(x, y, size):
    left = x
    right = x + size
    bottom = y + size
    top = y
    return left, top, right, bottom


def main():
    print("Starting Application")
    process_files()
    print("Processing complete!")


if __name__ == "__main__":
    main()
