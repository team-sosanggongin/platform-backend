package com.platform.sosangongin.services.version;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VersionCheckUtils {

    public static boolean isVersionInSpan(String versionCode, String currentAppVer) {
        throwIfArgsIsWrong(versionCode);
        throwIfArgsIsWrong(currentAppVer);

        List<Integer> versions = Arrays.stream(split(versionCode)).mapToInt(Integer::parseInt).boxed().toList();
        List<Integer> currents = Arrays.stream(split(currentAppVer)).mapToInt(Integer::parseInt).boxed().toList();

        if(!Objects.equals(versions.get(0), currents.get(0))){
            return false;
        }
        if(versions.get(1) - currents.get(1) >= 2 || versions.get(1) < currents.get(1)){
            return false;
        }

        return versions.get(2) >= currents.get(2);
    }

    private static void throwIfArgsIsWrong(String version) {
        if(version == null){
            throw new IllegalArgumentException("version must not be null");
        }
        String[] versions = version.split("\\.");

        if(versions.length != 3 ){
            throw new IllegalArgumentException("version should be in the form of {major}.{minor}.{bug-fix} but yours is "+version);
        }

        for (String s : versions) {
            try{
                Integer.parseInt(s);
            }catch (NumberFormatException e){
                throw new IllegalArgumentException("version must only contains number but yours is  "+version);
            }
        }
    }

    private static String[] split(String version){
        return version.split("\\.");
    }
}
