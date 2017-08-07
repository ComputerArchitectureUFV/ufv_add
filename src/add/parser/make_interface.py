from hades_parser import get_name, get_components
from make_components import *

def make_interface(top, data_width_ext, nameInterface):

    m = Module(nameInterface)
    m.Input('clk')
    m.Input('rst')
    m.Input('start')
    m.Input('num_conf', 32)
    m.Input('num_data_in', 32)
    m.Input('num_data_out', 32)
    m.Input('available_write')
    m.Input('available_read')
    m.Output('req_rd_data')
    m.Input('rd_data', data_width_ext)
    m.Output('req_wr_data')
    m.Output('wr_data', data_width_ext)
    m.Output('done')

    m.Instance(top,top.name, m.get_params(),m.get_ports())

    simulation.setup_waveform(m, nameInterface)

    return m