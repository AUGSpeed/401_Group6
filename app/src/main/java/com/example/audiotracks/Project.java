package com.example.audiotracks;

public class Project {
    String name = "";
    Track tracks[] = {};
    Integer index = 0;

    public void addTrack(Track track)
    {
        tracks[index] = track;
        index++;
    }

    public void removeTrack(Integer i)
    {
        if (i == index - 1)
        {
            //We are at the top of the array, simply set the top to null and lower index.
            tracks[i] = null;
            index--;
        }
        else if ( i < index - 1)
        {
            //Everything above needs to move down
            for (int j = i; j < index; j++)
            {
                tracks[j] = tracks[j+1];
            }
            //The top one can now disappear, since it is now a copy of the one below it
            tracks[index - 1] = null;
            index--;
        }
        else if ( i > index - 1)
        {
            //Don't do anything, we cannot remove this track, since it does not exist.
        }
    }

    public void setName(String newName)
    {
        name = newName;
    }
}
