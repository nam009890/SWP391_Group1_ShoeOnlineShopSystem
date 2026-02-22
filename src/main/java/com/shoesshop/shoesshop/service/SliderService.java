package com.shoesshop.shoesshop.service;

import com.shoesshop.shoesshop.entity.Slider;
import com.shoesshop.shoesshop.repository.SliderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SliderService {

    @Autowired
    private SliderRepository sliderRepository;

    @PostConstruct
    public void initMockData() {
        if (sliderRepository.count() == 0) {
            sliderRepository.save(new Slider(null, "Super sale 1/1", "", LocalDate.of(2024, 10, 25)));
            sliderRepository.save(new Slider(null, "Super sale 2/2", "", LocalDate.of(2024, 10, 25)));
            sliderRepository.save(new Slider(null, "Super sale 3/3", "", LocalDate.of(2024, 10, 25)));
            sliderRepository.save(new Slider(null, "Black Friday", "", LocalDate.of(2024, 10, 25)));
            sliderRepository.save(new Slider(null, "End Year sale", "", LocalDate.of(2024, 10, 25)));
            for (int i = 6; i <= 15; i++) {
                sliderRepository.save(new Slider(null, "Slider Test " + i, "Mô tả " + i, LocalDate.now()));
            }
        }
    }

    public Page<Slider> getSliders(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return sliderRepository.findAll(paging);
        } else {
            return sliderRepository.findByNameContainingIgnoreCase(keyword, paging);
        }
    }

    public void saveSlider(Slider slider) {
        if (slider.getCreateDate() == null) {
            slider.setCreateDate(LocalDate.now());
        }
        sliderRepository.save(slider);
    }

    public Slider getSliderById(Long id) {
        return sliderRepository.findById(id).orElse(null);
    }

    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }
}