package de.meinemod;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record GrowTreePayload(BlockPos pos, int boneMeal, int boneBlocks) implements CustomPayload {
    public static final Id<GrowTreePayload> ID = new Id<>(Identifier.of(MeineMod.MOD_ID, "grow_tree"));
    public static final PacketCodec<RegistryByteBuf, GrowTreePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, GrowTreePayload::pos,
            PacketCodecs.VAR_INT, GrowTreePayload::boneMeal,
            PacketCodecs.VAR_INT, GrowTreePayload::boneBlocks,
            GrowTreePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
