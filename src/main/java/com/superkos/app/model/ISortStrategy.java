package com.superkos.app.model;

import java.util.List;

public interface ISortStrategy {
    List<Hunian> sortasc(List<Hunian> hunianList);
    List<Hunian> sortdesc(List<Hunian> hunianList);
}
