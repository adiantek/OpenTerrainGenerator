package com.pg85.otg.gen.resource;

import com.pg85.otg.constants.Constants;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.interfaces.IBiomeConfig;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class WellResource extends FrequencyResourceBase
{
	private final int maxAltitude;
	private final int minAltitude;
	private final LocalMaterialData slab;
	private final LocalMaterialData water;
	private final LocalMaterialData material;
	private final MaterialSet sourceBlocks;

	public WellResource(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
	{
		super(biomeConfig, args, logger, materialReader);
		assureSize(8, args);

		this.material = readMaterial(args.get(0), materialReader);
		this.slab = readMaterial(args.get(1), materialReader);
		this.water = readMaterial(args.get(2), materialReader);
		this.frequency = readInt(args.get(3), 1, 100);
		this.rarity = readRarity(args.get(4));
		this.minAltitude = readInt(args.get(5), Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1);
		this.maxAltitude = readInt(args.get(6), this.minAltitude + 1, Constants.WORLD_HEIGHT - 1);
		this.sourceBlocks = readMaterials(args, 7, materialReader);
	}

	@Override
	public void spawn(IWorldGenRegion worldGenRegion, Random random, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
	{
		int y = random.nextInt(this.maxAltitude - this.minAltitude) + this.minAltitude;

		LocalMaterialData worldMaterial;
		while (
			y > this.minAltitude && 
			(worldMaterial = worldGenRegion.getMaterial(x, y, z, chunkBeingPopulated)) != null && 
			worldMaterial.isAir()
		)
		{
			--y;
		}

		worldMaterial = worldGenRegion.getMaterial(x, y, z, chunkBeingPopulated);
		if (worldMaterial == null || !this.sourceBlocks.contains(worldMaterial))
		{
			return;
		}
		
		int i;
		int j;

		for (i = -2; i <= 2; ++i)
		{
			for (j = -2; j <= 2; ++j)
			{
				if (
					(worldMaterial = worldGenRegion.getMaterial(x + i, y - 1, z + j, chunkBeingPopulated)) == null ||
					worldMaterial.isAir() ||
					(worldMaterial = worldGenRegion.getMaterial(x + i, y - 2, z + j, chunkBeingPopulated)) == null ||
					worldMaterial.isAir()
				)
				{
					return;
				}
			}
		}

		for (i = -1; i <= 0; ++i)
		{
			for (j = -2; j <= 2; ++j)
			{
				for (int var9 = -2; var9 <= 2; ++var9)
				{
					worldGenRegion.setBlock(x + j, y + i, z + var9, this.material, null, chunkBeingPopulated, false);
				}
			}
		}

		worldGenRegion.setBlock(x, y, z, this.water, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x - 1, y, z, this.water, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x + 1, y, z, this.water, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x, y, z - 1, this.water, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x, y, z + 1, this.water, null, chunkBeingPopulated, false);

		for (i = -2; i <= 2; ++i)
		{
			for (j = -2; j <= 2; ++j)
			{
				if (i == -2 || i == 2 || j == -2 || j == 2)
				{
					worldGenRegion.setBlock(x + i, y + 1, z + j, this.material, null, chunkBeingPopulated, false);
				}
			}
		}

		worldGenRegion.setBlock(x + 2, y + 1, z, this.slab, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x - 2, y + 1, z, this.slab, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x, y + 1, z + 2, this.slab, null, chunkBeingPopulated, false);
		worldGenRegion.setBlock(x, y + 1, z - 2, this.slab, null, chunkBeingPopulated, false);

		for (i = -1; i <= 1; ++i)
		{
			for (j = -1; j <= 1; ++j)
			{
				if (i == 0 && j == 0)
				{
					worldGenRegion.setBlock(x + i, y + 4, z + j, this.material, null, chunkBeingPopulated, false);
				} else {
					worldGenRegion.setBlock(x + i, y + 4, z + j, this.slab, null, chunkBeingPopulated, false);
				}
			}
		}

		for (i = 1; i <= 3; ++i)
		{
			worldGenRegion.setBlock(x - 1, y + i, z - 1, this.material, null, chunkBeingPopulated, false);
			worldGenRegion.setBlock(x - 1, y + i, z + 1, this.material, null, chunkBeingPopulated, false);
			worldGenRegion.setBlock(x + 1, y + i, z - 1, this.material, null, chunkBeingPopulated, false);
			worldGenRegion.setBlock(x + 1, y + i, z + 1, this.material, null, chunkBeingPopulated, false);
		}
	}
	
	@Override
	public String toString()
	{
		String output = "Well(" + this.material + "," + this.slab + "," + this.water + ",";
		output += this.frequency + "," + this.rarity + "," + this.minAltitude + "," + this.maxAltitude + this.makeMaterials(this.sourceBlocks) + ")";
		return output;
	}	
}
