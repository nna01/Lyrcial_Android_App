package com.lyrical.songuse;

import java.io.File;
import java.util.ArrayList;

public class GetSongFromDevice {

    boolean status=false;
    ArrayList<File> songlist=new ArrayList<File>();
    public GetSongFromDevice(){

    }
    public ArrayList<File> createList(File root){

        ArrayList<File> ara=new ArrayList<File>();

        File[] filelist=root.listFiles();

        for (File selectedsong : filelist)
        {
            if( !selectedsong.isHidden() && selectedsong.isDirectory()){
                ara.addAll(createList(selectedsong));
            }
            else {
                if(selectedsong.getName().endsWith(".mp3") || selectedsong.getName().endsWith(".wav")){
                    ara.add(selectedsong);
                }
            }
        }
        status=true;
        songlist=ara;
        return ara;
    }
    public boolean getstatus(){
        return status;
    }
    public ArrayList<File> listofsongs(){
        return songlist;
    }
}