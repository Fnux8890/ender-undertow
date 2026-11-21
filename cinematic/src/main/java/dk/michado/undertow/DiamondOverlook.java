package dk.michado.undertow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Inspect already-loaded sections once, when the Gentleman releases a miner. */
public final class DiamondOverlook {
    private static final int RANGE = 48;
    private static final TagKey<Block> FLOOR = TagKey.create(Registries.BLOCK, Identifier.parse("undertow:floor"));
    private static final TagKey<Block> AIR = TagKey.create(Registries.BLOCK, Identifier.parse("undertow:air"));
    private static final Identifier DESTINATION = Identifier.parse("undertow:overlook");

    private static boolean diamond(BlockState state) {
        return state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE);
    }

    public static int find(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos origin = player.blockPosition();
        int attempts = 0;
        for (int cx = (origin.getX()-RANGE)>>4; cx <= (origin.getX()+RANGE)>>4; cx++) {
            for (int cz = (origin.getZ()-RANGE)>>4; cz <= (origin.getZ()+RANGE)>>4; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) continue;
                int low = Math.max(level.getMinSectionY(), (origin.getY()-RANGE)>>4);
                int high = Math.min(level.getMaxSectionY(), (origin.getY()+RANGE)>>4);
                for (int sy = low; sy <= high; sy++) {
                    var section = chunk.getSection(level.getSectionIndexFromSectionY(sy));
                    if (!section.maybeHas(DiamondOverlook::diamond)) continue;
                    for (int index = 0; index < 4096; index++) {
                        int x = index & 15, z = (index >> 4) & 15, y = index >> 8;
                        if (!diamond(section.getBlockState(x,y,z))) continue;
                        BlockPos ore = new BlockPos(cx*16+x, sy*16+y, cz*16+z);
                        double distance = ore.distSqr(origin);
                        if (distance < 144 || distance > RANGE*RANGE || !exposed(level, ore)) continue;
                        if (++attempts > 32) return 0;
                        BlockPos feet = overlook(player, ore);
                        if (feet == null) continue;
                        CompoundTag dest = new CompoundTag();
                        dest.putInt("x", feet.getX()); dest.putInt("y", feet.getY()); dest.putInt("z", feet.getZ());
                        dest.putDouble("ox", ore.getX()+0.5); dest.putDouble("oy", ore.getY()+0.5); dest.putDouble("oz", ore.getZ()+0.5);
                        level.getServer().getCommandStorage().set(DESTINATION, dest);
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    private static boolean exposed(ServerLevel level, BlockPos ore) {
        for (Direction direction : Direction.values()) {
            BlockPos side = ore.relative(direction);
            if (level.hasChunkAt(side) && level.getBlockState(side).is(AIR)) return true;
        }
        return false;
    }

    private static BlockPos overlook(ServerPlayer player, BlockPos ore) {
        ServerLevel level = player.level();
        // Bound the line of sight and the clearance checks to loaded terrain.
        for (int cx = (ore.getX()-7)>>4; cx <= (ore.getX()+7)>>4; cx++) {
            for (int cz = (ore.getZ()-7)>>4; cz <= (ore.getZ()+7)>>4; cz++) {
                if (level.getChunkSource().getChunkNow(cx,cz) == null) return null;
            }
        }
        for (BlockPos feet : BlockPos.betweenClosed(ore.offset(-6,-4,-6), ore.offset(6,3,6))) {
            double distance = feet.distSqr(ore);
            if (distance < 9 || distance > 36 || !level.getWorldBorder().isWithinBounds(feet) || !walkable(level,feet)) continue;
            if (!level.getFluidState(feet.above(2)).isEmpty()) continue;
            boolean exit = false, lava = false;
            for (Direction d : Direction.Plane.HORIZONTAL) {
                BlockPos next = feet.relative(d);
                exit |= walkable(level,next);
                lava |= level.getBlockState(next).is(Blocks.LAVA) || level.getBlockState(next.above()).is(Blocks.LAVA);
            }
            if (lava || !exit) continue;
            Vec3 eyes = Vec3.atBottomCenterOf(feet).add(0,player.getEyeHeight(),0);
            var hit = level.clip(new ClipContext(eyes, Vec3.atCenterOf(ore), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
            if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(ore)) return feet.immutable();
        }
        return null;
    }

    private static boolean walkable(ServerLevel level, BlockPos feet) {
        return level.getBlockState(feet.below()).is(FLOOR) && level.getBlockState(feet).is(AIR) && level.getBlockState(feet.above()).is(AIR);
    }
}
