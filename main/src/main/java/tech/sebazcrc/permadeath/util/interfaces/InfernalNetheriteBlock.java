package tech.sebazcrc.permadeath.util.interfaces;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

public interface InfernalNetheriteBlock {

    default Location blockFaceToLocation(Block block, BlockFace face) {
        Location loc = block.getLocation();

        switch (face) {
            case DOWN:
                loc.setY(loc.getY() - 1);
                break;
            case EAST:
                loc.setX(loc.getX() + 1);
                break;
            case NORTH:
                loc.setZ(loc.getZ() - 1);
                break;
            case SOUTH:
                loc.setZ(loc.getZ() + 1);
                break;
            case UP:
                loc.setY(loc.getY() + 1);
                break;
            case WEST:
                loc.setX(loc.getX() - 1);
                break;
            default:
                break;
        }

        return loc;
    }

    void placeCustomBlock(Location pos);

    void onBlockBreak(BlockBreakEvent e);

    boolean isInfernalNetherite(Location pos);
}