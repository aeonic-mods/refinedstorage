package storagecraft.tile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.block.BlockCable;

public class TileCable extends TileSC {
	public static boolean isCable(World world, int x, int y, int z, ForgeDirection dir) {
		Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);

		return block instanceof BlockCable;
	}

	public boolean hasConnection(ForgeDirection dir) {
		if (!isCable(worldObj, xCoord, yCoord, zCoord, dir)) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

			return tile instanceof IMachine || tile instanceof TileController;
		}

		return true;
	}

	public List<IMachine> findMachines(TileController controller) {
		return findMachines(new ArrayList(), controller);
	}

	private List<IMachine> findMachines(List<Vec3> visited, TileController controller) {
		List<IMachine> machines = new ArrayList<IMachine>();

		for (Vec3 visitedBlock : visited) {
			if (visitedBlock.xCoord == xCoord && visitedBlock.yCoord == yCoord && visitedBlock.zCoord == zCoord) {
				return machines;
			}
		}

		visited.add(Vec3.createVectorHelper(xCoord, yCoord, zCoord));

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;

			boolean found = false;

			for (Vec3 visitedBlock : visited) {
				if (visitedBlock.xCoord == x && visitedBlock.yCoord == y && visitedBlock.zCoord == z) {
					found = true;
				}
			}

			if (found) {
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(x, y, z);

			if (tile instanceof IMachine) {
				machines.add((IMachine) tile);

				visited.add(Vec3.createVectorHelper(x, y, z));
			} else if (tile instanceof TileCable) {
				machines.addAll(((TileCable) tile).findMachines(visited, controller));
			} else if (tile instanceof TileController && (x != controller.xCoord || y != controller.yCoord || z != controller.zCoord)) {
				worldObj.createExplosion(null, x, y, z, 4.5f, true);
			}
		}

		return machines;
	}
}
