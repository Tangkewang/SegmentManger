package com.TKW.Data.FilmData;

import lombok.Data;

import java.util.List;

@Data
public class DataMess {
    private int total;
    private int limit;
    private int page;
    private List<FilmDataMess> subject;
}
