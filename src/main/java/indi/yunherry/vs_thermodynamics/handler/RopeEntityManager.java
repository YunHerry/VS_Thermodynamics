package indi.yunherry.vs_thermodynamics.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.yunherry.vs_thermodynamics.utils.ConnectionPointData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class RopeEntityManager {
    public static  final String SAVE_DATA_LIST = "save_data_list";
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ArrayList<RopeConstraintData> ropeConstraints = new ArrayList<>();
    public static RopeConstraintData putConstraint(RopeConstraintData ropeConstraintData) {
        ropeConstraints.add(ropeConstraintData);
        return ropeConstraintData;
    }
    private static class RopeEntityData extends SavedData {

        @Override
        public CompoundTag save(CompoundTag tag) {
            try {

                tag.putString(SAVE_DATA_LIST, objectMapper.writeValueAsString(ropeConstraints));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return tag;
        }

    }
//    public static RopeEntityManager loadOrCreate() {
////        MyWorldData.get(evt.getServer().overworld());
//    }
}
