package com.okc.superweapon.Mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

import static com.okc.superweapon.SuperWeapon.*;

@Mixin(PlayerEntity.class)
public abstract class playerMixin {

    @Inject(method = "attack", at = @At(value = "HEAD"), cancellable = true)
    private void modifyAttackRange(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getMainHandStack().getItem() == Items.DIAMOND_SWORD) {
            double attackRange = 45.0D;
            Box expandedBox = player.getBoundingBox().expand(attackRange, attackRange, attackRange);
            List<Entity> entities = player.world.getOtherEntities(player, expandedBox, entity -> entity != player);
            Vec3d d = getPlayerLookDirection(player);
            player.addVelocity(d.x * (-1.5), player.isOnGround() ? 4 : 1, d.z * (-1.5));
            player.velocityModified = true;
            for (Entity entity : entities) {
                if (player.squaredDistanceTo(entity) <= attackRange * attackRange) {
                    player.tryAttack(entity);
                    if (entity instanceof LivingEntity e) {
                        //System.out.println("b:" + e.getHealth());
                        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(player.getWorld());
                        if (lightning != null) {
                            lightning.setPos(e.getX(), e.getY(), e.getZ());
                            lightning.setCosmetic(false);
                            player.getWorld().spawnEntity(lightning);
                        }

                        e.setHealth(e.getHealth() - 8.0f);
                        e.addVelocity(d.x * 1.5, 3, d.z * 1.5);
                        e.velocityModified = true;
                        //System.out.println("a:" + e.getHealth());
                    }
                }
            }
        } else if (player.getMainHandStack().getItem() == Items.NETHERITE_SWORD) {;
            double attackRange = 35.0D;
            Box expandedBox = player.getBoundingBox().expand(attackRange, attackRange, attackRange);
            List<Entity> entities = player.world.getOtherEntities(player, expandedBox, entity -> entity != player);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity e) {
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(player.getWorld());
                    if (lightning != null) {
                        lightning.setPos(e.getX(), e.getY(), e.getZ());
                        lightning.setCosmetic(false);
                        player.getWorld().spawnEntity(lightning);
                    }
                    e.setHealth(e.getHealth() - 13.0f);
                }
            }
            Vec3d direction = getPlayerLookDirection(player);
            Vec3d playerPos = player.getPos();
            double k = direction.z / direction.x;
            double b = playerPos.z - k * playerPos.x;
            double y = player.getY();
            if (Math.abs(k) <= 1) {
                for (int i = 1; i <= 30; i++) {
                    //System.out.println(i);
                    double x = playerPos.x + i * (direction.x > 0 ? 1 : -1);
                    double z = k * x + b;
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(player.getWorld());
                    if (lightning != null) {
                        lightning.setPos(x, y, z);
                        lightning.setCosmetic(false);
                        player.getWorld().spawnEntity(lightning);
                    }
                }
            } else if (Math.abs(k) > 1){
                for (int i = 1; i <= 30; i++){
                    double z = playerPos.z + i * (direction.z > 0 ? 1 : -1);
                    double x = (z - b) / k;
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(player.getWorld());
                    if (lightning != null) {
                        lightning.setPos(x, y, z);
                        lightning.setCosmetic(false);
                        player.getWorld().spawnEntity(lightning);
                    }
                }
            }
            player.addVelocity(direction.x * (-1.5), 2.0, direction.z * (-1.5));
            player.velocityModified = true;
        }

    }
}
