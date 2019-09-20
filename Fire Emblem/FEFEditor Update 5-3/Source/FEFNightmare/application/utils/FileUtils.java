package application.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
    public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<>();
        if(!directory.exists())
            return resultList;

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList != null ? fList : new File[0]) {
            if (file.isFile()) {
                if(file.getName().toLowerCase().endsWith(".nmm"))
                    resultList.add(file);
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        return resultList;
    }
}
