
package com.example.audiotracks;

/**
 *  This is our Project class
 *  It stores variables for the project name
 *  as well as an array of references to where each individual
 *  audio track is stored.
 *  It also has setters and getters for those variables and
 *  a add and remove function for audio tracks
 *  @author Ehab Hanhan
 *  @author Michael LaRussa
 *  @author Koshiro Kawai
 *  @author Sahej Hundal
 *  @version 1.0
 */
public class Project {
    String name = "";
    String paths[] = {null, null, null};

    /**
     * addPath is used to store a new track
     * @param path each path is the address of a new track
     * @param index is an index used to iterate through a list of paths
     */
    public void addPath(String path, int index)
    {
        paths[index] = path;
    }

    /**
     *  removePath is used when a track is deleted from the project
     * @param index is an index used to iterate through a list of paths
     */
    public void removePath(int index)
    {
        if (index >= 0 && index <=2) {
            paths[index] = null;
        }
    }

    /**
     * getPath is used to return the address of a track
     * @param index iterator used to parse through each path
     * @return
     */
    public String getPath(int index)
    {
        if (index >= 0 && index <=2) {
            return paths[index];
        }
        return "Error";
    }

    /**
     *  setName sets the name of the project
     * @param newName new name of the project
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     *  getName returns the name of the project
     * @return name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * setPaths sets the path of a track to store it within the project
     * @param path the address of the track
     */
    public void setPaths(String path[]){
        paths = path;
    }

    /**
     *  getPaths returns the list of paths
     * @return list of paths
     */
    public String[] getPaths(){
        return paths;
    }
}
