package tech.sebazcrc.permadeath.nms.v1_20_R1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class CustomCreeper extends Creeper {
    private final boolean enderCreeper;

    public CustomCreeper(EntityType<? extends Creeper> type, Level world, boolean enderCreeper) {
        super(type, world);
        this.enderCreeper = enderCreeper;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (this.enderCreeper) {
            boolean flag = damagesource.getDirectEntity() instanceof ThrownPotion;
            boolean flag1;
            if (!damagesource.is(DamageTypeTags.IS_PROJECTILE) && !flag) {
                flag1 = super.hurt(damagesource, f);
                if (!this.level().isClientSide() && !(damagesource.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
                    this.teleport();
                }

                return flag1;
            } else {
                for(int i = 0; i < 64; ++i) {
                    if (this.teleport()) {
                        return true;
                    }
                }

                return super.hurt(damagesource, f);
            }
        }
        return super.hurt(damagesource, f);
    }


    public boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
            double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    private boolean teleport(double d0, double d1, double d2) {
        BlockPos.MutableBlockPos blockposition_mutableblockposition = new BlockPos.MutableBlockPos(d0, d1, d2);

        while(blockposition_mutableblockposition.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockposition_mutableblockposition).blocksMotion()) {
            blockposition_mutableblockposition.move(Direction.DOWN);
        }

        BlockState iblockdata = this.level().getBlockState(blockposition_mutableblockposition);
        boolean flag = iblockdata.blocksMotion();
        boolean flag1 = iblockdata.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            Vec3 vec3d = this.position();
            boolean flag2 = this.randomTeleport(d0, d1, d2, true);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound((Player)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }
}