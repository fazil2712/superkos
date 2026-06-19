package com.superkos.app.model;

import java.util.List;
// #adam(SearchEngine)
public interface ISearchFilter {
    List<Hunian> filter(List<Hunian> hunianList);
}
// #/adam(SearchEngine)
