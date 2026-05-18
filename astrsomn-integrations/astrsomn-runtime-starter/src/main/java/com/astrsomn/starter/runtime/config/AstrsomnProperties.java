package com.astrsomn.starter.runtime.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class AstrsomnProperties {

    
    private Boolean enabled = true;

    
    private String envCode;

    
    private String username;

    
    private String adminUsers;

    
    private String accountKey = "astrsomn-account-key";

    
    private MybatisPlus mybatisPlus = new MybatisPlus();

    
    private DataBase dataBase = new DataBase();

    @Data
    public static class MybatisPlus {

        
        private String additionalMapperLocations;

        
        private String additionalTypeAliasesPackage;
    }

    @Data
    public static class DataBase {

        
        private Validation validation = new Validation();
    }

    @Data
    public static class Validation {

        
        private Boolean enabled = true;

        
        private List<String> requiredTables = new ArrayList<>(List.of("SYS_ENV"));

        
        private Boolean failFast = false;

        
        private Integer connectionTimeoutSeconds;
    }
}
