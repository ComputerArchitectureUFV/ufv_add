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

    protected int[] histogram;
    protected int counter, decr;
    protected final int NUMBITS = 16;

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
        setString(Integer.toString(id), Integer.toString(immediate));
        return histogram[data]++;
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level. It sets the new text to be shown by the component. In this case
     * the id.
     */
    @Override
    public void reset() {
        setString(Integer.toString(id), Integer.toString(immediate));
        immediate = id;
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0;
        }
        counter = 0;
        decr = 0;
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
        if ((signalClk = portClk.getSignal()) != null) {
            SignalStdLogic1164 tick = (SignalStdLogic1164) portClk.getSignal();
            if (tick.hasRisingEdge()) {
                tickUp();
            } else if (tick.hasFallingEdge()) {
                tickDown();
            }
        }
        //Fim

        boolean isX = false;

        if ((signalClk = portClk.getSignal()) == null) {
            isX = true;
        } else if ((signalRst = portRst.getSignal()) == null) {
            isX = true;
        } else if ((signalEn = portEn.getSignal()) == null) {
            isX = true;
        } else if ((signalDconf = portDconf.getSignal()) == null) {
            isX = true;
        } else if ((signalDin = portDin.getSignal()) == null) {
            isX = true;
        } else if ((signalDout = portDout.getSignal()) == null) {
            isX = true;
        } else if ((signalRin = portRin.getSignal()) == null) {
            isX = true;
        }

        StdLogic1164 valueRst = portRst.getValueOrU();
        StdLogic1164 rOut;

        if (isX || valueRst.is_1()) {
            reset();

            //para portDout
            if ((signalDout = portDout.getSignal()) != null) { // get output
                vector = vector_UUU.copy();
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(new SimEvent(signalDout, time, vector, portDout));
            }
            //para portRout
            if ((signalRout = portRout.getSignal()) != null) { // get output
                rOut = new StdLogic1164(2);
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
            }
        } else {
            SignalStdLogic1164 clk = (SignalStdLogic1164) portClk.getSignal();
            StdLogic1164 en = portEn.getValueOrU();
            StdLogic1164 rIn = portRin.getValueOrU();

            if (clk.hasRisingEdge()) {
                if (en.is_1()) {

                    StdLogicVector valueDconf = portDconf.getVectorOrUUU();
                    if (!valueDconf.has_UXZ()) {
                        signalDconf = portDconf.getSignal();
                        StdLogicVector conf = (StdLogicVector) signalDconf.getValue();
                        int dImmediate, dId = (int) conf.getValue();
                        dImmediate = dId >> 8;
                        dId = dId & 0x000000ff;
                        if (dId == id) {
                            immediate = dImmediate;
                            setString(Integer.toString(id), Integer.toString(immediate));
                        }
                    }

                    signalDin = portDin.getSignal();
                    StdLogicVector dIn = (StdLogicVector) signalDin.getValue();

                    if (counter < immediate) {
                        if (rIn.is_1()) {
                            compute((int) dIn.getValue());
                            counter++;
                        }
                    } else if (decr < (int) Math.pow(2, 16)) {
                        StdLogicVector saida = new StdLogicVector(32);
                        saida.setValue(histogram[decr]);			//aqui ocorre a chamada para a computação da saída.
                        vector = saida.copy();
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(new SimEvent(signalDout, time, vector, portDout));
                        if ((signalRout = portRout.getSignal()) != null) { // get output
                            rOut = new StdLogic1164(3);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
                        }
                        decr++;
                    } else {
                        if ((signalRout = portRout.getSignal()) != null) { // get output
                            rOut = new StdLogic1164(2);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
                        }
                    }
                } else {
                    if ((signalRout = portRout.getSignal()) != null) { // get output
                        rOut = new StdLogic1164(2);
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
                    }
                    notCompute();
                }
            }
        }
    }
}
