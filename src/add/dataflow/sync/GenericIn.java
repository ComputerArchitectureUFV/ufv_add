package add.dataflow.sync;

import hades.models.Const1164;
import hades.models.PortStdLogic1164;
import hades.models.PortStdLogicVector;
import hades.models.StdLogic1164;
import hades.models.StdLogicVector;
import hades.models.rtlib.GenericRtlibObject.FlexibleLabelFormatter;
import hades.models.ruge.ColoredValueLabel;
import hades.signals.Signal;
import hades.signals.SignalStdLogic1164;
import hades.simulator.Port;
import hades.simulator.SimEvent;
import hades.simulator.SimEvent1164;
import hades.simulator.SimObject;
import hades.simulator.Simulatable;
import hades.simulator.Wakeable;
import hades.simulator.WakeupEvent;
import hades.symbols.BboxRectangle;
import hades.symbols.BusPortSymbol;
import hades.symbols.Label;
import hades.symbols.PortSymbol;
import hades.symbols.Rectangle;
import hades.symbols.Symbol;
import hades.utils.StringTokenizer;
import jfig.utils.SetupManager;

/**
 * GenericIn component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components that implement input
 * queues with 1, 2, 4, 8, 16, or 32 outputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericIn extends SimObject
        implements Simulatable, Wakeable,
        java.io.Serializable {

    //rtlibgeneric
    protected int n_bits = 16;
    protected StdLogicVector vector,
            vector_UUU, vector_XXX, vector_ZZZ,
            vector_000, vector_111;
    protected PortStdLogicVector vectorOutputPort;

    protected double delay;
    protected double defaultdelay = 10E-9;

    // Graphics stuff
    protected boolean enableAnimationFlag;
    protected ColoredValueLabel valueLabel;
    protected FlexibleLabelFormatter labelFormatter; // initially null
    //rtlibgeneric

    private String componentType = "_";

    final int QTDE_PORTS;
    final int TOT_PORTS;

    protected PortStdLogic1164 portClk, portRst, portEnOut, portRdy;
    protected PortStdLogicVector[] portDout;
    protected PortStdLogic1164[] portRout;
    protected PortStdLogicVector portDconf;

    protected int[] vectorIn;
    protected int idxDin;
    protected boolean start;

    /**
     * Object Constructor. By default, an input queue of an output is created.
     */
    public GenericIn() {
        super();
        QTDE_PORTS = 1;
        TOT_PORTS = (5 + (QTDE_PORTS * 2));
        init();
    }

    /**
     * Object Constructor. An input queue of N outputs is created.
     *
     * @param QTDE_PORTS - Number of queue outputs to be created
     */
    public GenericIn(int QTDE_PORTS) {
        super();
        this.QTDE_PORTS = QTDE_PORTS;
        TOT_PORTS = (5 + (QTDE_PORTS * 2));
        init();
    }

    /**
     * Method responsible for initializing the object at the time of its
     * construction.
     */
    private void init() {
        setCompName("IN");
        idxDin = 0;
        delay = defaultdelay;
        start = true;
        constructStandardValues();
        constructPorts();
        enableAnimationFlag = SetupManager.getBoolean("Hades.LayerTable.RtlibAnimation", false);
        vectorIn = new int[(QTDE_PORTS * 10) + 2];
        vectorIn[0] = 1;
        vectorIn[1] = 0x00000501;//Sends the value 5 for the component with ID 1 by default.
        for (int i = 2; i < vectorIn.length; i++) {
            vectorIn[i] = i - 2;
        }
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    public void constructPorts() {

        portClk = new PortStdLogic1164(this, "clk", Port.IN, null);
        portEnOut = new PortStdLogic1164(this, "en", Port.OUT, null);
        portRst = new PortStdLogic1164(this, "rst", Port.IN, null);
        portRdy = new PortStdLogic1164(this, "rdy", Port.IN, null);
        portDconf = new PortStdLogicVector(this, "dconf", Port.OUT, null, 32);

        portRout = new PortStdLogic1164[QTDE_PORTS];
        portDout = new PortStdLogicVector[QTDE_PORTS];

        for (int i = 0; i < QTDE_PORTS; i++) {
            portRout[i] = new PortStdLogic1164(this, "rout" + Integer.toString(i + 1), Port.OUT, null);
            portDout[i] = new PortStdLogicVector(this, "dout" + Integer.toString(i + 1), Port.OUT, null, 16);
        }

        ports = new Port[TOT_PORTS];

        int idx = 0;

        for (int i = 0; i < QTDE_PORTS; i++) {
            ports[idx] = portRout[i];
            idx++;
            ports[idx] = portDout[i];
            idx++;
        }

        ports[idx] = portClk;
        idx++;
        ports[idx] = portRst;
        idx++;
        ports[idx] = portEnOut;
        idx++;
        ports[idx] = portDconf;
        idx++;
        ports[idx] = portRdy;
    }

    /**
     * Method responsible to set the data vector to be delivered to the outputs.
     *
     * @param vector - Vector that will be delivered to the outputs
     */
    public void setVectorIn(int[] vector) {
        vectorIn = vector;
        System.out.println("Data Loaded from main class:");
        for (int i = 0; i < vectorIn.length; i++) {
            System.out.print(Integer.toString(vectorIn[i]) + " ");
        }
        System.out.println("");
    }

    /**
     * Method responsible for changing the label that displays the name of the
     * component.
     *
     * @param l - String to be set to the component name.
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
     * checked whether the ports are connected. It Will pass the vector data to
     * the outputs.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;
        boolean isX = false;

        Signal[] signalRout = new Signal[QTDE_PORTS];
        Signal[] signalDout = new Signal[QTDE_PORTS];
        Signal signalEnOut;
        Signal signalDconf = null;

        if (portClk.getSignal() == null) {
            isX = true;
        } else if (portRst.getSignal() == null) {
            isX = true;
        } else if (portEnOut.getSignal() == null) {
            isX = true;
        } else {
            for (int i = 0; i < QTDE_PORTS; i++) {
                if (portRout[i].getSignal() == null || portDout[i] == null) {
                    isX = true;
                }
            }
        }

        StdLogic1164 valueRst = portRst.getValueOrU();
        StdLogic1164 enOut;
        StdLogic1164[] rOut = new StdLogic1164[QTDE_PORTS];
        StdLogicVector[] saida = new StdLogicVector[QTDE_PORTS];

        if (isX || valueRst.is_1()) {
            idxDin = 0;
            start = true;

            for (int i = 0; i < QTDE_PORTS; i++) {
                //para portDout
                if ((signalDout[i] = portDout[i].getSignal()) != null) { // get output
                    saida[i] = new StdLogicVector(32);
                    saida[i] = vector_UUU.copy();
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(new SimEvent(signalDout[i], time, saida[i], portDout[i]));
                }

                //para portRout
                if ((signalRout[i] = portRout[i].getSignal()) != null) { // get output
                    rOut[i] = new StdLogic1164(2);
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout[i], time, rOut[i], portRout[i]));
                }
            }

            //para portEnOut
            if ((signalEnOut = portEnOut.getSignal()) != null) { // get output
                enOut = new StdLogic1164(2);
                time = simulator.getSimTime() + delay;
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalEnOut, time, enOut, portEnOut));
            }
        } else {

            SignalStdLogic1164 clk = (SignalStdLogic1164) portClk.getSignal();

            if (clk.hasRisingEdge()) {
                if (start) {
                    if (idxDin < vectorIn[0]) {
                        idxDin++;

                        if ((signalDconf = portDconf.getSignal()) != null) {
                            StdLogicVector conf_out = new StdLogicVector(32);
                            conf_out.setValue(vectorIn[idxDin]);			//aqui ocorre a chamada para a computação da saída.
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(new SimEvent(signalDconf, time, conf_out, portDconf));
                        }
                    } else {
                        idxDin++;
                        start = false;
                        if ((signalDconf = portDconf.getSignal()) != null) {
                            StdLogicVector conf_out = new StdLogicVector(32);
                            conf_out.setValue(0);			//aqui ocorre a chamada para a computação da saída.
                            time = simulator.getSimTime() + delay;
                            simulator.scheduleEvent(new SimEvent(signalDconf, time, conf_out, portDconf));
                        }
                    }
                } else {
                    for (int i = 0; i < QTDE_PORTS; i++) {
                        if (idxDin < vectorIn.length) {

                            //para portDout
                            if ((signalDout[i] = portDout[i].getSignal()) != null) { // get output
                                saida[i] = new StdLogicVector(32);
                                saida[i].setValue(vectorIn[idxDin]);
                                time = simulator.getSimTime() + delay;
                                simulator.scheduleEvent(new SimEvent(signalDout[i], time, saida[i], portDout[i]));
                            }

                            //para portRout
                            if ((signalRout[i] = portRout[i].getSignal()) != null) { // get output
                                rOut[i] = new StdLogic1164(3);
                                time = simulator.getSimTime() + delay;
                                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout[i], time, rOut[i], portRout[i]));
                            }
                            idxDin++;
                        } else {
                            //para portRout
                            if ((signalRout[i] = portRout[i].getSignal()) != null) { // get output
                                rOut[i] = new StdLogic1164(2);
                                time = simulator.getSimTime() + delay;
                                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout[i], time, rOut[i], portRout[i]));
                            }
                        }
                    }
                }

                //para portEnOut
                if ((signalEnOut = portEnOut.getSignal()) != null) { // get output
                    enOut = new StdLogic1164(3);
                    time = simulator.getSimTime() + delay;
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalEnOut, time, enOut, portEnOut));
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
     * @return - TRUE means that the symbol will be built dynamically.
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
        int X = (600 + (2 * 600));
        int Y = (600 + (QTDE_PORTS * 2 * 600));
        symbol = new Symbol();
        symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 0 " + Integer.toString(X) + " " + Integer.toString(Y));
        symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 " + Integer.toString(X) + " " + Integer.toString(Y));
        symbol.addMember(rec);

        Label label_nome = new Label();
        label_nome.initialize(Integer.toString(X / 2) + " " + Integer.toString(Y / 2) + " 2 " + componentType);
        symbol.addMember(label_nome);

        BusPortSymbol busportsymbol;
        PortSymbol portsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 " + Integer.toString(Y) + " " + portClk.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 " + Integer.toString(Y) + " " + portRst.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 0 " + portRdy.getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 0 " + portEnOut.getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 " + Integer.toString(Y) + " " + portDconf.getName());
        symbol.addMember(busportsymbol);

        Y = 600;

        for (int i = 0; i < QTDE_PORTS; i++) {
            portsymbol = new PortSymbol();
            portsymbol.initialize(Integer.toString(X) + " " + Integer.toString(Y) + " " + portRout[i].getName());
            symbol.addMember(portsymbol);
            Y += 600;

            busportsymbol = new BusPortSymbol();
            busportsymbol.initialize(Integer.toString(X) + " " + Integer.toString(Y) + " " + portDout[i].getName());
            symbol.addMember(busportsymbol);

            Y += 600;
        }
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
                + " " + QTDE_PORTS
                + " n");
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
            } else if (n_tokens == 3 || n_tokens == 4 || n_tokens == 5) {
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

    /**
     * Method responsible for creating some auxiliary variables for working with
     * bit vectors.
     */
    protected void constructStandardValues() {
        vector_UUU = new StdLogicVector(n_bits, Const1164.__U);
        vector_XXX = new StdLogicVector(n_bits, Const1164.__X);
        vector_ZZZ = new StdLogicVector(n_bits, Const1164.__Z);
        vector_000 = new StdLogicVector(n_bits, Const1164.__0);
        vector_111 = new StdLogicVector(n_bits, Const1164.__1);
        vector = vector_UUU.copy();
    }

    /**
     * Method responsible for returning the value of the delay variable that
     * contains the response delay time of the component.
     *
     * @return - Returns the dalay of the component.
     */
    public double getDelay() {
        return delay;
    }

    /**
     * Method responsible for changing the value of the delay variable that
     * contains the response delay time of the component.
     *
     * @param _delay
     */
    public void setDelay(double _delay) {
        if (_delay < 0) {
            delay = defaultdelay;
        } else {
            delay = _delay;
        }
    }

    /**
     * Method responsible for changing the value of the delay variable that
     * contains the response delay time of the component.
     *
     * @param s
     */
    public void setDelay(String s) {
        try {
            delay = new Double(s).doubleValue();
        } catch (Exception e) {
            message("-E- Illegal number format in String '" + s + "'");
            message("-w- Using default value: " + defaultdelay);
            delay = defaultdelay;
        }
    }

    /**
     * wakeup(): Called by the simulator as a reaction to our own
     * scheduleWakeup()-calls. For RTLIB components, a wakeup() is normally used
     * to update the value label on its graphical symbol. A WakeupEvent for this
     * purpose should have either 'null' or the current 'this' object as its
     * payload.
     * <p>
     * A second use is to update our internal 'vector' variable at a specified
     * simulation time, which is needed to implement the assign() method from
     * interface hades.simulator.Assignable. A WakeupEvent for this purpose is
     * expected to hold a StdLogicVector object (with the 'value' from the
     * assign call) as its payload.
     *
     * @param arg - Object to be awakened.
     */
    @Override
    public void wakeup(Object arg) {
        if (debug) {
            System.err.println(toString() + ".wakeup()");
        }
        try {
            WakeupEvent evt = (WakeupEvent) arg;
            Object tmp = evt.getArg();

            if (tmp instanceof StdLogicVector) { // called via assign: update vector
                StdLogicVector slv = (StdLogicVector) tmp;
                vector = slv.copy();
            } else { // 'traditional' wakeup: do nothing here, just update the symbol
                ;
            }
        } catch (Exception e) {
            System.err.println("-E- " + toString() + ".wakeup: " + arg);
        }
        if (enableAnimationFlag) {
            updateSymbol();
        }
    }

    /**
     * Method responsible for updating the component symbol.
     */
    public void updateSymbol() {
        if (debug) {
            message("-I- " + toString() + ".updateSymbol: " + vector);
        }

        if (!enableAnimationFlag) {
            return;
        }
        if (symbol == null || !symbol.isVisible()) {
            return;
        }

        //int    intValue = (int) vector.getValue();
        //Color  color    = Color_DIN_IEC_62.getColor( intValue );
        //if (valueLabel != null) valueLabel.setColor( color );
        if (symbol.painter == null) {
            return;
        }
        symbol.painter.paint(symbol, 50 /*msec*/);
    }
}
