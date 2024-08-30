package com.demo.folder.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.demo.folder.utils.StorageUtil.*;

@Component
public class StorageBean {

    private final Map<String,Map<Long, Object>> storage = new HashMap<>();

    public StorageBean() {
        storage.put(USERS_NAMESPACE,new HashMap<>());
        storage.put(TRAININGS_NAMESPACE,new HashMap<>());
        storage.put(TRAINEES_NAMESPACE,new HashMap<>());
        storage.put(TRAINERS_NAMESPACE, new HashMap<>());
    }

    public Map<Long,Object> getByNameSpace(String nameSpace) {
        return storage.get(nameSpace);
    }

    public void addNamespace(String namespace) {
        storage.putIfAbsent(namespace, new HashMap<>());
    }
}
