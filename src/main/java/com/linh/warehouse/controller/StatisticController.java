//package com.linh.warehouse.controller;
//
//import com.linh.warehouse.dto.response.StatisticResponse;
//import com.linh.warehouse.service.StatisticService;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@RestController
//@RequestMapping("/statistics")
//@RequiredArgsConstructor
//@FieldDefaults(level = PRIVATE, makeFinal = true)
//public class StatisticController {
//
//    StatisticService statisticService;
//
//    @GetMapping("/dashboard")
//    public StatisticResponse getDashboardStatistic(
//            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
//            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
//    ) {
//        return statisticService.getDashboardStatistic(fromDate, toDate);
//    }
//}
