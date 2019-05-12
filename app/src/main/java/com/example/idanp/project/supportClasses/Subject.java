package com.example.idanp.project.supportClasses;


import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;
import com.example.idanp.project.supportClasses.baseClasses.BaseSubject;

import java.util.ArrayList;
import java.util.List;

public class Subject extends BaseSubject {
    private static final String TAG = "Subject";

    public Subject(String name) {
        super(name);
    }

    public Subject(){

    }

    public void createChart(AnyChartView l_chart){
        Log.d(TAG, "Creating " + name + " chart");
        Cartesian l_cartesian = AnyChart.line();

        l_cartesian.animation(true);

        l_cartesian.padding(10d, 20d, 5d, 20d);

        l_cartesian.crosshair().enabled(true);
        l_cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        l_cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        l_cartesian.title("Grades of " + name);

        l_cartesian.yAxis(0).title("Grade");
        l_cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<ValueDataEntry> l_seriesData = new ArrayList<>();
        createDataEntryList(l_seriesData);
        Set l_set = Set.instantiate();
        Mapping l_mapping = l_set.mapAs("{ x: 'x', value: 'value' }");
        Line l_series = l_cartesian.line(l_mapping);
        l_series.name(name);
        l_series.hovered().markers().enabled(true);
        l_series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        l_series.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        l_cartesian.legend().enabled(true);
        l_cartesian.legend().fontSize(13d);
        l_cartesian.legend().padding(0d, 0d, 10d, 0d);

        l_chart.setChart(l_cartesian);

    }

    private void createDataEntryList(List<ValueDataEntry> l_seriesData){
        for(BaseGrade g : grades){
            l_seriesData.add(new ValueDataEntry(((Grade) g).getDate().toString(), g.getGrade()));
        }
    }

    public ArrayList<String> getGradesString(){
        ArrayList<String> list = new ArrayList<>();
        for(BaseGrade grade : grades){
            list.add(grade.getGrade() + "");
        }
        return list;
    }

    public ArrayList<String> getDistrebutionsString(){
        ArrayList<String> list = new ArrayList<>();
        for(BaseGrade grade : grades){
            list.add(grade.getDistribution() + "");
        }
        return list;
    }

}
