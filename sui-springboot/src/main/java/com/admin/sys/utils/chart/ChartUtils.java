package com.admin.sys.utils.chart;

import com.admin.sys.utils.chart.data.DataBend;
import com.admin.sys.utils.chart.data.DataLine;
import com.admin.sys.utils.chart.data.DataPie;

public class ChartUtils {
    public static DataLine createLine(){
        DataLine dataLine = new DataLine();
        return dataLine;
    }
    public static DataPie createPie(){
        DataPie dataPie = new DataPie();
        return dataPie;
    }
    public static DataBend createBend(){
        DataBend dataBend = new DataBend();
        return dataBend;
    }
}
