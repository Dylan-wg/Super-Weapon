package com.okc.superweapon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;

public class SuperWeapon implements ModInitializer {
	private static Double vy;
	public static boolean isLanding;

	static {
		isLanding = false;
	}

	@Override
	public void onInitialize() {

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null) {
				vy = client.player.getVelocity().getY();
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				if (vy < -0.08 && client.player.isOnGround()) {
					isLanding = true;
				} else {
					isLanding = false;
				}
				vy = client.player.getVelocity().getY();
			}
		});

		/*
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			System.out.println(isLanding);
		});
		 */

	}

	public static Vec3d getPlayerLookDirection(PlayerEntity player) {
		float yaw = player.getYaw();
		float pitch = player.getPitch();

		double yawRad = Math.toRadians(-yaw);
		double pitchRad = Math.toRadians(-pitch);

		double x = Math.cos(pitchRad) * Math.sin(yawRad);
		double y = Math.sin(pitchRad);
		double z = Math.cos(pitchRad) * Math.cos(yawRad);

		return new Vec3d(x, y, z);
	}

}
