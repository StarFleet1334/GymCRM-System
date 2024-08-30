package com.demo.folder.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StorageBean {

    private final Map<String,Map<Long, Object>> storage = new HashMap<>();

    public StorageBean() {
        storage.put("users",new HashMap<>());
        storage.put("trainers",new HashMap<>());
        storage.put("trainees",new HashMap<>());
        storage.put("trainings", new HashMap<>());
    }

    public Map<Long,Object> getByNameSpace(String nameSpace) {
        return storage.get(nameSpace);
    }

    public void addNamespace(String namespace) {
        storage.putIfAbsent(namespace, new HashMap<>());
    }
}
