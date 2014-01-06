package eu.verdelhan.tailtest.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.verdelhan.tailtest.OperationType;
import eu.verdelhan.tailtest.TimeSeries;
import eu.verdelhan.tailtest.Trade;

public class CashFlow {

    private final TimeSeries timeSeries;

    private final List<Trade> trades;

    private List<Double> values;

    public CashFlow(TimeSeries timeSeries, List<Trade> trades) {
        this.timeSeries = timeSeries;
        this.trades = trades;
        values = new ArrayList<Double>();
        values.add(1d);
        calculate();
    }

    public double getValue(int index) {
        return values.get(index);
    }

    public int getSize() {
        return timeSeries.getSize();
    }

    /**
     * Calculates the cash flow.
     */
    private void calculate() {

        for (Trade trade : trades) {
            // For each trade...
            int begin = trade.getEntry().getIndex() + 1;
            if (begin > values.size()) {
                values.addAll(Collections.nCopies(begin - values.size(), values.get(values.size() - 1)));
            }
            int end = trade.getExit().getIndex();
            for (int i = Math.max(begin, 1); i <= end; i++) {
                double ratio;
                if (trade.getEntry().getType().equals(OperationType.BUY)) {
                    ratio = timeSeries.getTick(i).getClosePrice()
                            / timeSeries.getTick(trade.getEntry().getIndex()).getClosePrice();
                } else {
                    ratio = timeSeries.getTick(trade.getEntry().getIndex()).getClosePrice()
                            / timeSeries.getTick(i).getClosePrice();
                }
                values.add(values.get(trade.getEntry().getIndex()) * ratio);
            }
        }
        if ((timeSeries.getEnd() - values.size()) >= 0) {
            values.addAll(Collections.nCopies((timeSeries.getEnd() - values.size()) + 1, values.get(values.size() - 1)));
        }
    }

}
