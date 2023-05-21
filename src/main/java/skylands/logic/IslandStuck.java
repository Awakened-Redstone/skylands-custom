package skylands.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class IslandStuck {
    public ArrayList<Island> stuck = new ArrayList<>();

    public Island create(PlayerEntity player) {
        for (var island : this.stuck) {
            if (island.owner.uuid.equals(player.getUuid())) return island;
        }
        var island = new Island(player);
        island.freshCreated = true;
        this.stuck.add(island);
        return island;
    }

    public void delete(PlayerEntity player) {
        this.getByPlayer(player).ifPresent(island -> {
            island.getNetherHandler().delete();
            island.getEndHandler().delete();
            island.getHandler().delete();
        });
        stuck.removeIf(island -> island.owner.uuid.equals(player.getUuid()));
    }

    public void delete(String playerName) {
        this.getByPlayer(playerName).ifPresent(island -> {
            island.getNetherHandler().delete();
            island.getEndHandler().delete();
            island.getHandler().delete();
        });
        stuck.removeIf(island -> island.owner.name.equals(playerName));
    }

    public Optional<Island> getByPlayer(PlayerEntity player) {
        for (var island : this.stuck) {
            if (island.owner.uuid.equals(player.getUuid())) return Optional.of(island);
        }
        return Optional.empty();
    }

    public Optional<Island> getByPlayer(String playerName) {
        for (var island : this.stuck) {
            if (island.owner.name.equals(playerName)) return Optional.of(island);
        }
        return Optional.empty();
    }

    public Optional<Island> getByPlayer(UUID playerUuid) {
        for (var island : this.stuck) {
            if (island.owner.uuid.equals(playerUuid)) return Optional.of(island);
        }
        return Optional.empty();
    }

    public Optional<Island> get(UUID islandId) {
        for (var island : this.stuck) {
            if (island.getIslandId().equals(islandId)) return Optional.of(island);
        }
        return Optional.empty();
    }

    public Optional<Island> getFromMember(PlayerEntity player) {
        for (var island : this.stuck) {
            if (island.owner.uuid.equals(player.getUuid())) return Optional.of(island);
            else if (island.isMember(player)) return Optional.of(island);
        }
        return Optional.empty();
    }

    public boolean hasIsland(UUID uuid) {
        for (var island : this.stuck) {
            if (island.owner.uuid.equals(uuid)) return true;
        }
        return false;
    }

    public void readFromNbt(NbtCompound nbt) {
        NbtCompound islandStuckNbt = nbt.getCompound("islandStuck");
        int size = islandStuckNbt.getInt("size");
        for (int i = 0; i < size; i++) {
            NbtCompound islandNbt = islandStuckNbt.getCompound(String.valueOf(i));
            Island island = Island.fromNbt(islandNbt);
            if (!this.hasIsland(island.owner.uuid)) {
                this.stuck.add(island);
            }
        }
    }

    public void writeToNbt(NbtCompound nbt) {
        NbtCompound islandStuckNbt = new NbtCompound();
        islandStuckNbt.putInt("size", this.stuck.size());
        for (int i = 0; i < this.stuck.size(); i++) {
            Island island = this.stuck.get(i);
            islandStuckNbt.put(Integer.toString(i), island.toNbt());
        }
        nbt.put("islandStuck", islandStuckNbt);
    }
}
