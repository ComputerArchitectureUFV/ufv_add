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
    private int n_bits = 16;
    private StdLogicVector vector;

    private StdLogicVector vector_UUU;
    private StdLogicVector vector_XXX;

    // Graphics stuff
    private StdLogicVector vector_ZZZ;
    private StdLogicVector vector_000;
    private StdLogicVector vector_111; // initially null
    //rtlibgeneric
    private PortStdLogicVector vectorOutputPort;
    private double delay;
    private double defaultdelay = 10E-9;
    private boolean enableAnimationFlag;
    private ColoredValueLabel valueLabel;
    private FlexibleLabelFormatter labelFormatter;

    private String componentType = "_";

    private final int QTDE_PORTS;
    private final int TOT_PORTS;

    private PortStdLogic1164 portClk;
    private PortStdLogic1164 portRst;
    private PortStdLogic1164 portEnOut;

    private PortStdLogic1164 portRdy;
    private PortStdLogicVector[] portDout;
    private PortStdLogic1164[] portRout;
    private PortStdLogicVector portDconf;
    private int[] vectorIn;
    private int idxDin;
    private boolean start;

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
        setIdxDin(0);
        setDelay(getDefaultdelay());
        setStart(true);
        constructStandardValues();
        constructPorts();
        setEnableAnimationFlag(SetupManager.getBoolean("Hades.LayerTable.RtlibAnimation", false));
        setVectorIn(new int[(getQTDE_PORTS() * 10) + 2]);
        getVectorIn()[0] = 1;
        getVectorIn()[1] = 0x00000501;//Sends the value 5 for the component with ID 1 by default.
        for (int i = 2; i < getVectorIn().length; i++) {
            getVectorIn()[i] = i - 2;
        }
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    public void constructPorts() {

        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortEnOut(new PortStdLogic1164(this, "en", Port.OUT, null));
        setPortRst(new PortStdLogic1164(this, "rst", Port.IN, null));
        setPortRdy(new PortStdLogic1164(this, "rdy", Port.IN, null));
        setPortDconf(new PortStdLogicVector(this, "dconf", Port.OUT, null, 32));

        setPortRout(new PortStdLogic1164[getQTDE_PORTS()]);
        setPortDout(new PortStdLogicVector[getQTDE_PORTS()]);

        for (int i = 0; i < getQTDE_PORTS(); i++) {
            getPortRout()[i] = new PortStdLogic1164(this, "rout" + Integer.toString(i + 1), Port.OUT, null);
            getPortDout()[i] = new PortStdLogicVector(this, "dout" + Integer.toString(i + 1), Port.OUT, null, 16);
        }

        ports = new Port[getTOT_PORTS()];

        int idx = 0;

        for (int i = 0; i < getQTDE_PORTS(); i++) {
            ports[idx] = getPortRout()[i];
            idx++;
            ports[idx] = getPortDout()[i];
            idx++;
        }

        ports[idx] = getPortClk();
        idx++;
        ports[idx] = getPortRst();
        idx++;
        ports[idx] = getPortEnOut();
        idx++;
        ports[idx] = getPortDconf();
        idx++;
        ports[idx] = getPortRdy();
    }

    /**
     * Method responsible to set the data vector to be delivered to the outputs.
     *
     * @param vectorIn - Vector that will be delivered to the outputs
     */
    public void setVectorIn(int[] vectorIn) {
        this.vectorIn = vectorIn;
        System.out.println("Data Loaded from main class:");
        for (int i = 0; i < getVectorIn().length; i++) {
            System.out.print(Integer.toString(getVectorIn()[i]) + " ");
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
            this.setComponentType(".");
        } else {
            this.setComponentType(l);
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

        Signal[] signalRout = new Signal[getQTDE_PORTS()];
        Signal[] signalDout = new Signal[getQTDE_PORTS()];
        Signal signalEnOut;
        Signal signalDconf = null;

        if (getPortClk().getSignal() == null) {
            isX = true;
        } else if (getPortRst().getSignal() == null) {
            isX = true;
        } else if (getPortEnOut().getSignal() == null) {
            isX = true;
        } else {
            for (int i = 0; i < getQTDE_PORTS(); i++) {
                if (getPortRout()[i].getSignal() == null || getPortDout()[i] == null) {
                    isX = true;
                }
            }
        }

        StdLogic1164 valueRst = getPortRst().getValueOrU();
        StdLogic1164 enOut;
        StdLogic1164[] rOut = new StdLogic1164[getQTDE_PORTS()];
        StdLogicVector[] saida = new StdLogicVector[getQTDE_PORTS()];

        if (isX || valueRst.is_1()) {
            setIdxDin(0);
            setStart(true);

            for (int i = 0; i < getQTDE_PORTS(); i++) {
                //para portDout
                if ((signalDout[i] = getPortDout()[i].getSignal()) != null) { // get output
                    saida[i] = new StdLogicVector(32);
                    saida[i] = getVector_UUU().copy();
                    time = simulator.getSimTime() + getDelay();
                    simulator.scheduleEvent(new SimEvent(signalDout[i], time, saida[i], getPortDout()[i]));
                }

                //para portRout
                if ((signalRout[i] = getPortRout()[i].getSignal()) != null) { // get output
                    rOut[i] = new StdLogic1164(2);
                    time = simulator.getSimTime() + getDelay();
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout[i], time, rOut[i], getPortRout()[i]));
                }
            }

            //para portEnOut
            if ((signalEnOut = getPortEnOut().getSignal()) != null) { // get output
                enOut = new StdLogic1164(2);
                time = simulator.getSimTime() + getDelay();
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalEnOut, time, enOut, getPortEnOut()));
            }
        } else {

            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();

            if (clk.hasRisingEdge()) {
                if (isStart()) {
                    if (getIdxDin() < getVectorIn()[0]) {
                        setIdxDin(getIdxDin() + 1);

                        if ((signalDconf = getPortDconf().getSignal()) != null) {
                            StdLogicVector conf_out = new StdLogicVector(32);
                            conf_out.setValue(getVectorIn()[getIdxDin()]);			//aqui ocorre a chamada para a computação da saída.
                            time = simulator.getSimTime() + getDelay();
                            simulator.scheduleEvent(new SimEvent(signalDconf, time, conf_out, getPortDconf()));
                        }
                    } else {
                        setIdxDin(getIdxDin() + 1);
                        setStart(false);
                        if ((signalDconf = getPortDconf().getSignal()) != null) {
                            StdLogicVector conf_out = new StdLogicVector(32);
                            conf_out.setValue(0);			//aqui ocorre a chamada para a computação da saída.
                            time = simulator.getSimTime() + getDelay();
                            simulator.scheduleEvent(new SimEvent(signalDconf, time, conf_out, getPortDconf()));
                        }
                    }
                } else {
                    for (int i = 0; i < getQTDE_PORTS(); i++) {
                        if (getIdxDin() < getVectorIn().length) {

                            //para portDout
                            if ((signalDout[i] = getPortDout()[i].getSignal()) != null) { // get output
                                saida[i] = new StdLogicVector(32);
                                saida[i].setValue(getVectorIn()[getIdxDin()]);
                                time = simulator.getSimTime() + getDelay();
                                simulator.scheduleEvent(new SimEvent(signalDout[i], time, saida[i], getPortDout()[i]));
                            }

                            //para portRout
                            if ((signalRout[i] = getPortRout()[i].getSignal()) != null) { // get output
                                rOut[i] = new StdLogic1164(3);
                                time = simulator.getSimTime() + getDelay();
                                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout[i], time, rOut[i], getPortRout()[i]));
                            }
                            setIdxDin(getIdxDin() + 1);
                        } else {
                            //para portRout
                            if ((signalRout[i] = getPortRout()[i].getSignal()) != null) { // get output
                                rOut[i] = new StdLogic1164(2);
                                time = simulator.getSimTime() + getDelay();
                                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRout[i], time, rOut[i], getPortRout()[i]));
                            }
                        }
                    }
                }

                //para portEnOut
                if ((signalEnOut = getPortEnOut().getSignal()) != null) { // get output
                    enOut = new StdLogic1164(3);
                    time = simulator.getSimTime() + getDelay();
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalEnOut, time, enOut, getPortEnOut()));
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
        int Y = (600 + (getQTDE_PORTS() * 2 * 600));
        symbol = new Symbol();
        symbol.setParent(this);

        BboxRectangle bbr = new BboxRectangle();
        bbr.initialize("0 0 " + Integer.toString(X) + " " + Integer.toString(Y));
        symbol.addMember(bbr);

        Rectangle rec = new Rectangle();
        rec.initialize("0 0 " + Integer.toString(X) + " " + Integer.toString(Y));
        symbol.addMember(rec);

        Label label_nome = new Label();
        label_nome.initialize(Integer.toString(X / 2) + " " + Integer.toString(Y / 2) + " 2 " + getComponentType());
        symbol.addMember(label_nome);

        BusPortSymbol busportsymbol;
        PortSymbol portsymbol;

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 " + Integer.toString(Y) + " " + getPortClk().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 " + Integer.toString(Y) + " " + getPortRst().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("1200 0 " + getPortRdy().getName());
        symbol.addMember(portsymbol);

        portsymbol = new PortSymbol();
        portsymbol.initialize("600 0 " + getPortEnOut().getName());
        symbol.addMember(portsymbol);

        busportsymbol = new BusPortSymbol();
        busportsymbol.initialize("1800 " + Integer.toString(Y) + " " + getPortDconf().getName());
        symbol.addMember(busportsymbol);

        Y = 600;

        for (int i = 0; i < getQTDE_PORTS(); i++) {
            portsymbol = new PortSymbol();
            portsymbol.initialize(Integer.toString(X) + " " + Integer.toString(Y) + " " + getPortRout()[i].getName());
            symbol.addMember(portsymbol);
            Y += 600;

            busportsymbol = new BusPortSymbol();
            busportsymbol.initialize(Integer.toString(X) + " " + Integer.toString(Y) + " " + getPortDout()[i].getName());
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
                + " " + getN_bits()
                + " " + getDelay()
                + " " + getQTDE_PORTS()
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
                setN_bits(16);
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 1) {
                versionId = Integer.parseInt(st.nextToken());
                setN_bits(16);
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 2) {
                versionId = Integer.parseInt(st.nextToken());
                setN_bits(Integer.parseInt(st.nextToken()));
                constructStandardValues();
                constructPorts();
            } else if (n_tokens == 3 || n_tokens == 4 || n_tokens == 5) {
                versionId = Integer.parseInt(st.nextToken());
                setN_bits(Integer.parseInt(st.nextToken()));
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
        setVector_UUU(new StdLogicVector(getN_bits(), Const1164.__U));
        setVector_XXX(new StdLogicVector(getN_bits(), Const1164.__X));
        setVector_ZZZ(new StdLogicVector(getN_bits(), Const1164.__Z));
        setVector_000(new StdLogicVector(getN_bits(), Const1164.__0));
        setVector_111(new StdLogicVector(getN_bits(), Const1164.__1));
        setVector(getVector_UUU().copy());
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
            delay = getDefaultdelay();
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
            setDelay(new Double(s).doubleValue());
        } catch (Exception e) {
            message("-E- Illegal number format in String '" + s + "'");
            message("-w- Using default value: " + getDefaultdelay());
            setDelay(getDefaultdelay());
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
                setVector(slv.copy());
            } else { // 'traditional' wakeup: do nothing here, just update the symbol
                ;
            }
        } catch (Exception e) {
            System.err.println("-E- " + toString() + ".wakeup: " + arg);
        }
        if (isEnableAnimationFlag()) {
            updateSymbol();
        }
    }

    /**
     * Method responsible for updating the component symbol.
     */
    public void updateSymbol() {
        if (debug) {
            message("-I- " + toString() + ".updateSymbol: " + getVector());
        }

        if (!isEnableAnimationFlag()) {
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

    /**
     * @return the n_bits
     */
    public int getN_bits() {
        return n_bits;
    }

    /**
     * @param n_bits the n_bits to set
     */
    public void setN_bits(int n_bits) {
        this.n_bits = n_bits;
    }

    /**
     * @return the vector
     */
    public StdLogicVector getVector() {
        return vector;
    }

    /**
     * @param vector the vector to set
     */
    public void setVector(StdLogicVector vector) {
        this.vector = vector;
    }

    /**
     * @return the vector_UUU
     */
    public StdLogicVector getVector_UUU() {
        return vector_UUU;
    }

    /**
     * @param vector_UUU the vector_UUU to set
     */
    public void setVector_UUU(StdLogicVector vector_UUU) {
        this.vector_UUU = vector_UUU;
    }

    /**
     * @return the vector_XXX
     */
    public StdLogicVector getVector_XXX() {
        return vector_XXX;
    }

    /**
     * @param vector_XXX the vector_XXX to set
     */
    public void setVector_XXX(StdLogicVector vector_XXX) {
        this.vector_XXX = vector_XXX;
    }

    /**
     * @return the vector_ZZZ
     */
    public StdLogicVector getVector_ZZZ() {
        return vector_ZZZ;
    }

    /**
     * @param vector_ZZZ the vector_ZZZ to set
     */
    public void setVector_ZZZ(StdLogicVector vector_ZZZ) {
        this.vector_ZZZ = vector_ZZZ;
    }

    /**
     * @return the vector_000
     */
    public StdLogicVector getVector_000() {
        return vector_000;
    }

    /**
     * @param vector_000 the vector_000 to set
     */
    public void setVector_000(StdLogicVector vector_000) {
        this.vector_000 = vector_000;
    }

    /**
     * @return the vector_111
     */
    public StdLogicVector getVector_111() {
        return vector_111;
    }

    /**
     * @param vector_111 the vector_111 to set
     */
    public void setVector_111(StdLogicVector vector_111) {
        this.vector_111 = vector_111;
    }

    /**
     * @return the vectorOutputPort
     */
    public PortStdLogicVector getVectorOutputPort() {
        return vectorOutputPort;
    }

    /**
     * @param vectorOutputPort the vectorOutputPort to set
     */
    public void setVectorOutputPort(PortStdLogicVector vectorOutputPort) {
        this.vectorOutputPort = vectorOutputPort;
    }

    /**
     * @return the defaultdelay
     */
    public double getDefaultdelay() {
        return defaultdelay;
    }

    /**
     * @param defaultdelay the defaultdelay to set
     */
    public void setDefaultdelay(double defaultdelay) {
        this.defaultdelay = defaultdelay;
    }

    /**
     * @return the enableAnimationFlag
     */
    public boolean isEnableAnimationFlag() {
        return enableAnimationFlag;
    }

    /**
     * @param enableAnimationFlag the enableAnimationFlag to set
     */
    public void setEnableAnimationFlag(boolean enableAnimationFlag) {
        this.enableAnimationFlag = enableAnimationFlag;
    }

    /**
     * @return the valueLabel
     */
    public ColoredValueLabel getValueLabel() {
        return valueLabel;
    }

    /**
     * @param valueLabel the valueLabel to set
     */
    public void setValueLabel(ColoredValueLabel valueLabel) {
        this.valueLabel = valueLabel;
    }

    /**
     * @return the labelFormatter
     */
    public FlexibleLabelFormatter getLabelFormatter() {
        return labelFormatter;
    }

    /**
     * @param labelFormatter the labelFormatter to set
     */
    public void setLabelFormatter(FlexibleLabelFormatter labelFormatter) {
        this.labelFormatter = labelFormatter;
    }

    /**
     * @return the componentType
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * @param componentType the componentType to set
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    /**
     * @return the QTDE_PORTS
     */
    public int getQTDE_PORTS() {
        return QTDE_PORTS;
    }

    /**
     * @return the TOT_PORTS
     */
    public int getTOT_PORTS() {
        return TOT_PORTS;
    }

    /**
     * @return the portClk
     */
    public PortStdLogic1164 getPortClk() {
        return portClk;
    }

    /**
     * @param portClk the portClk to set
     */
    public void setPortClk(PortStdLogic1164 portClk) {
        this.portClk = portClk;
    }

    /**
     * @return the portRst
     */
    public PortStdLogic1164 getPortRst() {
        return portRst;
    }

    /**
     * @param portRst the portRst to set
     */
    public void setPortRst(PortStdLogic1164 portRst) {
        this.portRst = portRst;
    }

    /**
     * @return the portEnOut
     */
    public PortStdLogic1164 getPortEnOut() {
        return portEnOut;
    }

    /**
     * @param portEnOut the portEnOut to set
     */
    public void setPortEnOut(PortStdLogic1164 portEnOut) {
        this.portEnOut = portEnOut;
    }

    /**
     * @return the portRdy
     */
    public PortStdLogic1164 getPortRdy() {
        return portRdy;
    }

    /**
     * @param portRdy the portRdy to set
     */
    public void setPortRdy(PortStdLogic1164 portRdy) {
        this.portRdy = portRdy;
    }

    /**
     * @return the portDout
     */
    public PortStdLogicVector[] getPortDout() {
        return portDout;
    }

    /**
     * @param portDout the portDout to set
     */
    public void setPortDout(PortStdLogicVector[] portDout) {
        this.portDout = portDout;
    }

    /**
     * @return the portRout
     */
    public PortStdLogic1164[] getPortRout() {
        return portRout;
    }

    /**
     * @param portRout the portRout to set
     */
    public void setPortRout(PortStdLogic1164[] portRout) {
        this.portRout = portRout;
    }

    /**
     * @return the portDconf
     */
    public PortStdLogicVector getPortDconf() {
        return portDconf;
    }

    /**
     * @param portDconf the portDconf to set
     */
    public void setPortDconf(PortStdLogicVector portDconf) {
        this.portDconf = portDconf;
    }

    /**
     * @return the vectorIn
     */
    public int[] getVectorIn() {
        return vectorIn;
    }

    /**
     * @return the idxDin
     */
    public int getIdxDin() {
        return idxDin;
    }

    /**
     * @param idxDin the idxDin to set
     */
    public void setIdxDin(int idxDin) {
        this.idxDin = idxDin;
    }

    /**
     * @return the start
     */
    public boolean isStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(boolean start) {
        this.start = start;
    }
}
