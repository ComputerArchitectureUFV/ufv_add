package add.dataflow.sync;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;

/**
 * Histogram component for the UFV synchronous data flow simulator.<br>
 * The component is responsible for computing the amount of times a given value
 * is delivered at its input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Histogram extends GenericI {

    private int[] histogram;
    private int counter;
    private int decr;
    private int NUMBITS = 16;

    /**
     * Object Constructor.
     */
    public Histogram() {
        super();
        setCompName("HIST");
        histogram = new int[(int) Math.pow(2, NUMBITS)];
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0;
        }
        counter = 0;
        decr = 0;
    }

    /**
     * Method responsible for the component computation: in this case it
     * performs the logical operation "AND" between the parameter and the
     * (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case the result
     * of the logical operation "AND" between the parameter and the id.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return getHistogram()[data]++;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     */
    @Override
    public void reset() {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        setImmediate(getId());
        for (int i = 0; i < getHistogram().length; i++) {
            getHistogram()[i] = 0;
        }
        setCounter(0);
        setDecr(0);
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events.
     *
     * In this case, it will be checked whether the ports are connected and will
     * execute the compute (int data) method if the R_IN input is high level. It
     * will execute the reset(), tickUp(), and tickDown() methods if their
     * respective entries order it. It will update the output with the
     * compute(int data) method result.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;

        Signal signalClk = null, signalRst = null, signalDin = null, signalDout = null, signalEn = null;
        Signal signalRin = null, signalRout = null, signalDconf = null;

        //código para tick_up e Tick_down
        if ((signalClk = getPortClk().getSignal()) != null) {
            SignalStdLogic1164 tick = (SignalStdLogic1164) getPortClk().getSignal();
            if (tick.hasRisingEdge()) {
                tickUp();
            } else if (tick.hasFallingEdge()) {
                tickDown();
            }
        }
        //Fim

        boolean isX = false;

        if ((signalClk = getPortClk().getSignal()) == null) {
            isX = true;
        } else if ((signalRst = getPortRst().getSignal()) == null) {
            isX = true;
        } else if ((signalEn = getPortEn().getSignal()) == null) {
            isX = true;
        } else if ((signalDconf = getPortDconf().getSignal()) == null) {
            isX = true;
        } else if ((signalDin = getPortDin().getSignal()) == null) {
            isX = true;
        } else if ((signalDout = getPortDout().getSignal()) == null) {
            isX = true;
        } else if ((signalRin = getPortRin().getSignal()) == null) {
            isX = true;
        }

        StdLogic1164 valueRst = getPortRst().getValueOrU();
        StdLogic1164 rOut;

        if (isX || valueRst.is_1()) {
            reset();

            //para portDout
            if ((signalDout = getPortDout().getSignal()) != null) { // get output
                vector = vector_UUU.copy();
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(new SimEvent(signalDout, time, vector, getPortDout()));
            }
            //para portRout
            if ((signalRout = getPortRout().getSignal()) != null) { // get output
                rOut = new StdLogic1164(2);
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
            }
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();
            StdLogic1164 en = getPortEn().getValueOrU();
            StdLogic1164 rIn = getPortRin().getValueOrU();

            if (clk.hasRisingEdge()) {
                if (en.is_1()) {

                    StdLogicVector valueDconf = getPortDconf().getVectorOrUUU();
                    if (!valueDconf.has_UXZ()) {
                        signalDconf = getPortDconf().getSignal();
                        StdLogicVector conf = (StdLogicVector) signalDconf.getValue();
                        int dImmediate, dId = (int) conf.getValue();
                        dImmediate = dId >> 8;
                        dId = dId & 0x000000ff;
                        if (dId == getId()) {
                            setImmediate(dImmediate);
                            setString(Integer.toString(getId()), Integer.toString(getImmediate()));
                        }
                    }

                    signalDin = getPortDin().getSignal();
                    StdLogicVector dIn = (StdLogicVector) signalDin.getValue();

                    if (getCounter() < getImmediate()) {
                        if (rIn.is_1()) {
                            compute((int) dIn.getValue());
                            setCounter(getCounter() + 1);
                        }
                    } else if (getDecr() < (int) Math.pow(2, 16)) {
                        StdLogicVector saida = new StdLogicVector(32);
                        saida.setValue(getHistogram()[getDecr()]);			//aqui ocorre a chamada para a computação da saída.
                        vector = saida.copy();
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(new SimEvent(signalDout, time, vector, getPortDout()));
                        if ((signalRout = getPortRout().getSignal()) != null) { // get output
                            rOut = new StdLogic1164(3);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                        }
                        setDecr(getDecr() + 1);
                    } else {
                        if ((signalRout = getPortRout().getSignal()) != null) { // get output
                            rOut = new StdLogic1164(2);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                        }
                    }
                } else {
                    if ((signalRout = getPortRout().getSignal()) != null) { // get output
                        rOut = new StdLogic1164(2);
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, getPortRout()));
                    }
                    notCompute();
                }
            }
        }
    }

    /**
     * @return the histogram
     */
    public int[] getHistogram() {
        return histogram;
    }

    /**
     * @param histogram the histogram to set
     */
    public void setHistogram(int[] histogram) {
        this.setHistogram(histogram);
    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * @return the decr
     */
    public int getDecr() {
        return decr;
    }

    /**
     * @param decr the decr to set
     */
    public void setDecr(int decr) {
        this.decr = decr;
    }

    /**
     * @return the NUMBITS
     */
    public int getNUMBITS() {
        return NUMBITS;
    }

    /**
     * @param NUMBITS the NUMBITS to set
     */
    public void setNUMBITS(int NUMBITS) {
        this.NUMBITS = NUMBITS;
    }
}
