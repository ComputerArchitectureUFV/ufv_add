package add.dataflow.sync;

import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;
import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;

/**
 * GenericBin component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components with two inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericBin extends hades.models.rtlib.GenericRtlibObject {

    protected Label stringLabel, labelNome;
    protected String componentType, s;

    protected PortStdLogic1164 portClk, portRst, portRin1, portRin2, portRout, portEn;
    protected PortStdLogicVector portDin1, portDin2, portDout;

    /**
     * Object Constructor.
     */
    public GenericBin() {
        super();
        setCompName("GEN_BIN");
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    @Override
    public void constructPorts() {
        portClk = new PortStdLogic1164(this, "clk", Port.IN, null);
        portRst = new PortStdLogic1164(this, "rst", Port.IN, null);
        portEn = new PortStdLogic1164(this, "en", Port.IN, null);
        portRin1 = new PortStdLogic1164(this, "rin1", Port.IN, null);
        portRin2 = new PortStdLogic1164(this, "rin2", Port.IN, null);
        portRout = new PortStdLogic1164(this, "rout", Port.IN, null);
        portDin1 = new PortStdLogicVector(this, "din1", Port.IN, null, new Integer(n_bits));
        portDin2 = new PortStdLogicVector(this, "din2", Port.IN, null, new Integer(n_bits));
        portDout = new PortStdLogicVector(this, "dout", Port.OUT, null, new Integer(n_bits));

        ports = new Port[9];
        ports[0] = portClk;
        ports[1] = portRst;
        ports[2] = portEn;
        ports[3] = portRin1;
        ports[4] = portRin2;
        ports[5] = portRout;
        ports[6] = portDin1;
        ports[7] = portDin2;
        ports[8] = portDout;
    }

    /**
     * Method responsible for updating the text displayed by the component.
     *
     * @param s - Text to be updated.
     */
    public void setString(String s) {
        this.s = s;
        stringLabel.setText(s);
        labelNome.setText(getName());
        getSymbol().painter.paint(getSymbol(), 100);
    }

    /**
     * Method responsible for updating the component symbol.
     *
     * @param s
     */
    @Override
    public void setSymbol(Symbol s) {
        symbol = s;
    }

    /**
     * Method responsible for the computation of the output.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 1.
     * @return - Return of computation
     */
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1));
        return data1;
    }

    /**
     * Method executed when computing is not performed. In this case it clears
     * the text displayed by the component.
     */
    public void notCompute() {
        setString("NULL");
    }

    /**
     * Method executed when the signal from the reset input goes to high logic
     * level.In this case it clears the text displayed by the component.
     */
    public void reseted() {
        s = "NULL";
        setString(s);
    }

    /**
     * Method executed when the clock signal goes to high logic level.
     */
    public void tickUp() {

    }

    /**
     * Method executed when the clock signal goes to low logic level.
     */
    public void tickDown() {

    }

    /**
     * Method responsible for changing the label that displays the name of the
     * component.
     */
    public void setCompName(String l) {
        if (l.equals("")) {
            this.componentType = ".";
        } else {
            this.componentType = l;
        }
    }

    /**
     * evaluate(): called by the simulation engine on all events that concern
     * this object. The object is responsible for updating its internal state
     * and for scheduling all pending output events. In this case, it will be
     * checked whether the ports are connected and will execute the compute (int
     * data) method if the R_IN (1 and 2) inputs are high level. It will execute
     * the reseted(), tickUp(), and tickDown() methods if their respective
     * entries order it. It will update the output with the compute(int data)
     * method result.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;

        Signal signalClk = null, signalRst = null, signalEn = null;
        Signal signalRin1 = null, signalRin2 = null, signalRout = null;
        Signal signalDin1 = null, signalDin2 = null, signalDout = null;

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
        } else if ((signalRin1 = portRin1.getSignal()) == null) {
            isX = true;
        } else if ((signalRin2 = portRin2.getSignal()) == null) {
            isX = true;
        } else if ((signalDin1 = portDin1.getSignal()) == null) {
            isX = true;
        } else if ((signalDin2 = portDin2.getSignal()) == null) {
            isX = true;
        } else if ((signalDout = portDout.getSignal()) == null) {
            isX = true;
        }

        StdLogic1164 value_RST = portRst.getValueOrU();
        StdLogic1164 rOut;

        if (isX || value_RST.is_1()) {
            reseted();

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
            StdLogic1164 rIn1 = portRin1.getValueOrU();
            StdLogic1164 rIn2 = portRin2.getValueOrU();

            if (clk.hasRisingEdge()) {
                if (en.is_1()) {

                    signalDin1 = portDin1.getSignal();
                    StdLogicVector dIn1 = (StdLogicVector) signalDin1.getValue();

                    signalDin2 = portDin2.getSignal();
                    StdLogicVector dIn2 = (StdLogicVector) signalDin2.getValue();

                    if ((rIn1.is_1()) && (rIn2.is_1())) {
                        StdLogicVector saida = new StdLogicVector(32);
                        saida.setValue(compute((int) dIn1.getValue(), (int) dIn2.getValue()));//aqui ocorre a chamada para a computação da saída.
                        vector = saida.copy();
                        time = simulator.getSimTime() + delay;
                        simulator.scheduleEvent(new SimEvent(signalDout, time, vector, portDout));
                        if ((signalRout = portRout.getSignal()) != null) { // get output
                            rOut = new StdLogic1164(3);
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout, time, rOut, portRout));
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

    /**
     * Method responsible for indicating to the simulator that the component's
     * symbol will be constructed dynamically by the constructDynamicSymbol()
     * method, or will be read from a file of the same name as the ".sym"
     * extension.
     *
     * @return - - TRUE means that the symbol will be built dynamically.
     */
    @Override
    public boolean needsDynamicSymbol() {
        return true;
    }

    /**
     * Method responsible for dynamically constructing the component symbol.
     */
    @Override
    public void constructDynamicSymbol() {
        symbol = new Symbol();
        symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("-200 -900 2000 3200");
        symbol.addMember(bbr);
        Rectangle rec = new Rectangle();
        rec.initialize("0 0 1800 3000");
        symbol.addMember(rec);

        PortSymbol portsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 3000 " + portClk.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 3000 " + portRst.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 0 " + portEn.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 600 " + portRin1.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("0 1800 " + portRin2.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1800 600 " + portRout.getName());
        symbol.addMember(portsymbol);

        BusPortSymbol busportsymbol;

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 1200 " + portDin1.getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("0 2400 " + portDin2.getName());
        symbol.addMember(busportsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 1200 " + portDout.getName());
        symbol.addMember(busportsymbol);

        labelNome = new Label();
        labelNome.initialize("0 -600 " + getName());
        symbol.addMember(labelNome);

        Label label0 = new Label();
        label0.initialize("900 600 2 " + componentType);
        symbol.addMember(label0);

        stringLabel = new Label();
        stringLabel.initialize("0 -200 " + s);

        symbol.addMember(stringLabel);

    }

    /**
     * Method responsible for writing component settings to the file saved by
     * the simulator.
     *
     * @param ps -Simulator writing object.
     */
    @Override
    public void write(java.io.PrintWriter ps) {
        ps.print(" " + versionId
                + " " + n_bits
                + " " + delay
                + " b");
    }

    /**
     * Method responsible for reading the component settings in the file saved
     * by the simulator.
     *
     * @param s - Settings for the component read from the file saved by the
     * simulator.
     * @return - Returns true if the settings are read successfully.
     */
    @Override
    public boolean initialize(String s) {
        StringTokenizer st = new StringTokenizer(s);
        int n_tokens = st.countTokens();
        try {
            if (n_tokens == 0) {
                versionId = 1001;
                n_bits = 16;
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 1) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = 16;
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 2) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 3 || n_tokens == 4) {
                versionId = Integer.parseInt(st.nextToken());
                n_bits = Integer.parseInt(st.nextToken());
                constructStandardValues();
                constructPorts();
                setDelay(st.nextToken());
            } else {
                throw new Exception("invalid number of arguments");
            }
        } catch (Exception e) {
            message("-E- " + toString() + ".initialize(): " + e + " " + s);
            e.printStackTrace();
        }
        return true;
    }
}
