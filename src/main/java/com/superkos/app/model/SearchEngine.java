package com.superkos.app.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SearchEngine implements ISearchFilter, ISortStrategy {
    private String lokasi;
    private double harga;
    private String tipeGender;
    private Date availableDateStart;
    private Date availableDateEnd;

    @Override
    public List<Hunian> sortdesc(List<Hunian> hunianList) {
        return hunianList.stream()
                .sorted(Comparator.comparing(Hunian::getHarga).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Hunian> sortasc(List<Hunian> hunianList) {
        return hunianList.stream()
                .sorted(Comparator.comparing(Hunian::getHarga))
                .collect(Collectors.toList());
    }

    @Override
    public List<Hunian> filter(List<Hunian> hunianList) {
        return hunianList.stream()
                .filter(h -> (lokasi == null || h.getLokasi().equalsIgnoreCase(lokasi)))
                .filter(h -> (harga == 0 || h.getHarga() <= harga))
                .filter(h -> (tipeGender == null || h.getTipeGender().equalsIgnoreCase(tipeGender)))
                // Available if property starts on/before requested start AND ends on/after requested end
                .filter(h -> (availableDateStart == null || (h.getAvailableDateStart() != null && !h.getAvailableDateStart().after(availableDateStart))))
                .filter(h -> (availableDateEnd == null || (h.getAvailableDateEnd() != null && !h.getAvailableDateEnd().before(availableDateEnd))))
                .collect(Collectors.toList());
    }

    public List<Hunian> search(List<Hunian> data) {
        // Core search mechanism implementation
        return null;
    }
}
