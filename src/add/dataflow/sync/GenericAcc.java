package add.dataflow.sync;

import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;

/**
 * GenericAcc component for the ADD Accelerator Design and Deploy.<br>
 * The component implements a generic accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericAcc extends GenericI {

    protected int acc;
    protected int counter;

    /**
     * Object Constructor.
     */
    public GenericAcc() {
        super();
        acc = 0;
        immediate = id;
        counter = immediate;
        setCompName("Generic_ACC");
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component and de
     * accumulator.
     */
    @Override
    public void reset() {
        acc = 0;
        immediate = id;
        counter = immediate;
    }

    /**
     * Method responsible for performing the accumulation or not.
     *
     * @param data - Value to be used for the computation.
     */
    protected void accumulate(int data) {
        acc = acc + data;
        setString(Integer.toString(id), Integer.toString(immediate));
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method if the R_IN input is high level. It will execute the
     * reset(), tickUp(), and tickDown() methods if their respective entries
     * order it. It will update the output with the ACC value when the
     * computation finishes.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;

        Signal signalClk = null, signalEn = null, signalRst = null, signalDin = null;
        Signal signalDout = null, signalRin = null, signalRout = null, signalDconf = null;

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
                        StdLogicVector dConf = (StdLogicVector) signalDconf.getValue();
                        int dImmediate, dId = (int) dConf.getValue();
                        dImmediate = dId >> 8;
                        dId = dId & 0x000000ff;
                        if (dId == id) {
                            immediate = dImmediate;
                            counter = immediate;
                            setString(Integer.toString(id), Integer.toString(immediate));
                        }
                    }
                    signalDin = portDin.getSignal();
                    StdLogicVector dIn = (StdLogicVector) signalDin.getValue();
                    if (counter > 1 && rIn.is_1()) {
                        accumulate((int) dIn.getValue());
                        counter--;

                        //para portRout
                        if ((signalRout = portRout.getSignal()) != null) { // get output
                            rOut = new StdLogic1164(2);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
                        }
                    } else {
                        if (counter <= 1 && rIn.is_1()) {
                            accumulate((int) dIn.getValue());
                            StdLogicVector saida = new StdLogicVector(32);
                            saida.setValue(acc);			//aqui ocorre a chamada para a computação da saída.
                            vector = saida.copy();
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(new SimEvent(signalDout, time, vector, portDout));
                            if ((signalRout = portRout.getSignal()) != null) { // get output
                                rOut = new StdLogic1164(3);
                                time = simulator.getSimTime() + delay;
                                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
                            }
                            counter = immediate;
                            acc = 0;
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
    }
}
