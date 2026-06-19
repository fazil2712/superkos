package com.superkos.app.model;

import java.util.List;
// #adam(SearchEngine)
public interface ISortStrategy {
    List<Hunian> sortasc(List<Hunian> hunianList);
    List<Hunian> sortdesc(List<Hunian> hunianList);
}
// #/adam(SearchEngine)
