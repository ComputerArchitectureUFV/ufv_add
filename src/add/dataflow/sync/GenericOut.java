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
 * GenericOut component for the ADD Accelerator Design and Deploy.<br>
 * The component creates the basis for other components that implement output
 * queues with 1, 2, 4, 8, 16, or 32 inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class GenericOut extends SimObject
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

    private PortStdLogic1164 portRdy;
    private PortStdLogic1164 portEn;
    private PortStdLogicVector[] portDin;
    private PortStdLogic1164[] portRin;

    private int[] vectorOut;
    private int idxDout;
    private int tamVectorOut;
    private boolean done = false;
    private int qtdeSave;

    /**
     * Object Constructor. By default, an input queue of an output is created.
     */
    public GenericOut() {
        super();
        QTDE_PORTS = 1;
        TOT_PORTS = (4 + (QTDE_PORTS * 2));
        init();
    }

    /**
     * Object Constructor. An output queue of N inputs is created.
     *
     * @param QTDE_PORTS - Number of queue inputs to be created
     */
    public GenericOut(int QTDE_PORTS) {
        super();
        this.QTDE_PORTS = QTDE_PORTS;
        TOT_PORTS = (4 + (QTDE_PORTS * 2));
        init();
    }

    /**
     * Method responsible for initializing the object at the time of its
     * construction.
     */
    private void init() {
        setCompName("OUT");
        setDelay(getDefaultdelay());
        constructStandardValues();
        constructPorts();
        setEnableAnimationFlag(SetupManager.getBoolean("Hades.LayerTable.RtlibAnimation", false));
        setTamVectorOut(0);
        setDone(false);
        qtdeSave = 0;
    }

    /**
     * Method responsible for initializing the component input and output ports.
     *
     */
    public void constructPorts() {

        setPortClk(new PortStdLogic1164(this, "clk", Port.IN, null));
        setPortRst(new PortStdLogic1164(this, "rst", Port.IN, null));
        setPortEn(new PortStdLogic1164(this, "en", Port.IN, null));
        setPortRdy(new PortStdLogic1164(this, "rdy", Port.OUT, null));

        setPortRin(new PortStdLogic1164[getQTDE_PORTS()]);
        setPortDin(new PortStdLogicVector[getQTDE_PORTS()]);

        for (int i = 0; i < getQTDE_PORTS(); i++) {
            getPortRin()[i] = new PortStdLogic1164(this, "rin" + Integer.toString(i + 1), Port.IN, null);
            getPortDin()[i] = new PortStdLogicVector(this, "din" + Integer.toString(i + 1), Port.IN, null, 16);
        }

        ports = new Port[getTOT_PORTS()];

        int idx = 0;

        for (int i = 0; i < getQTDE_PORTS(); i++) {
            ports[idx] = getPortRin()[i];
            idx++;
            ports[idx] = getPortDin()[i];
            idx++;
        }

        ports[idx] = getPortClk();
        idx++;
        ports[idx] = getPortRst();
        idx++;
        ports[idx] = getPortRdy();
        idx++;
        ports[idx] = getPortEn();
    }

    /**
     * Method responsible for returning end of data entry.
     *
     * @return - Returns the value of done signal.
     */
    public boolean getDoneSignal() {
        return isDone();
    }

    /**
     *
     * @param qtde_save
     */
    public void setQtdeSave(int qtde_save) {
        qtdeSave = qtde_save;
    }

    /**
     * Method responsible for inserting elements into the vector.
     *
     * @param k - Value to be inserted in vector.
     */
    public void setVector(int k) {
        int[] tmp;
        setTamVectorOut(getTamVectorOut() + 1);
        tmp = new int[getTamVectorOut()];
        for (int i = 0; i < getTamVectorOut() - 1; i++) {
            tmp[i] = vectorOut[i];
        }
        tmp[getTamVectorOut() - 1] = k;
        setVectorOut(new int[getTamVectorOut()]);
        for (int i = 0; i < getTamVectorOut(); i++) {
            vectorOut[i] = (int) tmp[i];
        }
    }

    /**
     * Method responsible for returning the data vector received by the queue
     * entries.
     *
     * @return - Returns the vector with the processed data.
     */
    public int[] getVectorOut() {
        return vectorOut;
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
     * checked whether the ports are connected and if the R_IN inputs are high
     * level. It Will pass the data from the inputs to the vector.
     *
     * @param arg an arbitrary object argument
     */
    @Override
    public void evaluate(Object arg) {

        double time;
        boolean isX = false;

        Signal signalRdy, signalEn;
        Signal[] signalRin = new Signal[getQTDE_PORTS()];
        Signal[] signalDin = new Signal[getQTDE_PORTS()];
        StdLogic1164[] rIn = new StdLogic1164[getQTDE_PORTS()];

        if (getPortClk().getSignal() == null) {
            isX = true;
        } else if (getPortRst().getSignal() == null) {
            isX = true;
        } else if (getPortRdy().getSignal() == null) {
            isX = true;
        } else if (getPortEn().getSignal() == null) {
            isX = true;
        } else {
            for (int i = 0; i < getQTDE_PORTS(); i++) {
                if (getPortRin()[i].getSignal() == null || getPortDin()[i] == null) {
                    isX = true;
                }
            }
        }

        StdLogic1164 value_RST = getPortRst().getValueOrU();
        StdLogic1164 rdy;

        if (isX || value_RST.is_1()) {
            setIdxDout(0);
            setDone(false);
            setTamVectorOut(0);

            //para portRdy
            if ((signalRdy = getPortRdy().getSignal()) != null) { // get output
                rdy = new StdLogic1164(2);
                time = simulator.getSimTime() + getDelay();
                simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRdy, time, rdy, getPortRdy()));
            }
        } else {

            SignalStdLogic1164 clk = (SignalStdLogic1164) getPortClk().getSignal();
            StdLogic1164 en = getPortEn().getValueOrU();

            if (clk.hasRisingEdge()) {
                if (en.is_1()) {
                    for (int i = 0; i < getQTDE_PORTS(); i++) {

                        rIn[i] = getPortRin()[i].getValueOrU();
                        signalDin[i] = getPortDin()[i].getSignal();
                        StdLogicVector d_in = (StdLogicVector) signalDin[i].getValue();
                        if (getQtdeSave() == 0) {
                            setDone(true);
                        } else if (rIn[i].is_1() && !isDone()) {
                            setVector((int) d_in.getValue());
                            qtdeSave--;
                        }
                    }
                }

                //para portRdy
                if ((signalRdy = getPortRdy().getSignal()) != null) { // get output
                    rdy = new StdLogic1164(3);
                    time = simulator.getSimTime() + getDelay();
                    simulator.scheduleEvent(SimEvent1164.createNewSimEvent(signalRdy, time, rdy, getPortRdy()));
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
        portsymbol.initialize("600 0 " + getPortEn().getName());
        symbol.addMember(portsymbol);

        Y = 600;

        for (int i = 0; i < getQTDE_PORTS(); i++) {
            portsymbol = new PortSymbol();
            portsymbol.initialize("0 " + Integer.toString(Y) + " " + getPortRin()[i].getName());
            symbol.addMember(portsymbol);
            Y += 600;

            busportsymbol = new BusPortSymbol();
            busportsymbol.initialize("0 " + Integer.toString(Y) + " " + getPortDin()[i].getName());
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
     * @return - Returns component delay
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
     * @return the portEn
     */
    public PortStdLogic1164 getPortEn() {
        return portEn;
    }

    /**
     * @param portEn the portEn to set
     */
    public void setPortEn(PortStdLogic1164 portEn) {
        this.portEn = portEn;
    }

    /**
     * @return the portDin
     */
    public PortStdLogicVector[] getPortDin() {
        return portDin;
    }

    /**
     * @param portDin the portDin to set
     */
    public void setPortDin(PortStdLogicVector[] portDin) {
        this.portDin = portDin;
    }

    /**
     * @return the portRin
     */
    public PortStdLogic1164[] getPortRin() {
        return portRin;
    }

    /**
     * @param portRin the portRin to set
     */
    public void setPortRin(PortStdLogic1164[] portRin) {
        this.portRin = portRin;
    }

    /**
     * @param vectorOut the vectorOut to set
     */
    public void setVectorOut(int[] vectorOut) {
        this.vectorOut = vectorOut;
    }

    /**
     * @return the idxDout
     */
    public int getIdxDout() {
        return idxDout;
    }

    /**
     * @param idxDout the idxDout to set
     */
    public void setIdxDout(int idxDout) {
        this.idxDout = idxDout;
    }

    /**
     * @return the tamVectorOut
     */
    public int getTamVectorOut() {
        return tamVectorOut;
    }

    /**
     * @param tamVectorOut the tamVectorOut to set
     */
    public void setTamVectorOut(int tamVectorOut) {
        this.tamVectorOut = tamVectorOut;
    }

    /**
     * @return the done
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @param done the done to set
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * @return the qtdeSave
     */
    public int getQtdeSave() {
        return qtdeSave;
    }
}
