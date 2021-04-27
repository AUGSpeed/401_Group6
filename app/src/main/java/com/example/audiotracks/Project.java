package com.example.audiotracks;

public class Project {
    String name = "";
    String paths[] = {null, null, null};

    public void addPath(String path, int index)
    {
        paths[index] = path;
    }

    public void removePath(int index)
    {
        if (index >= 0 && index <=2) {
            paths[index] = null;
        }
    }

    public String getPath(int index)
    {
        if (index >= 0 && index <=2) {
            return paths[index];
        }
        return "Error";
    }

    public void setName(String newName)
    {
        name = newName;
    }
    public String getName() {
        return name;
    }

    public void setPaths(String path[]){
        paths = path;
    }

    public String[] getPaths(){
        return paths;
    }
}
