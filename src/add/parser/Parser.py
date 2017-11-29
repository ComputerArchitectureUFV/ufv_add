from hades_parser import get_hds_string
from make_dataflow import make_dataflow
from make_interface import make_interface

'''
Obs: Para rodar é preciso instalar a lib veriloggen, não instalar a do repositorio do python
baixa ela direto do git https://github.com/PyHDI/veriloggen/. 

Outra observação, é necessário substituir o arquivo to_verilog.py instalado pela biblioteca pelo
arquivo to_verilog do repositório.
'''

#Modificar as linhas abaixo
hds = "/home/jeronimo/Área de Trabalho/add_ab.hds"
verilogPath = "/home/jeronimo/VERILOG/ADD_AB/rtl/"
#-------------------------------------------------

data_width_ext = 512
hds_string = get_hds_string(hds)
m = make_dataflow(hds_string,data_width_ext)
make_interface(m, data_width_ext,'uut_interface').to_verilog(verilogPath)
