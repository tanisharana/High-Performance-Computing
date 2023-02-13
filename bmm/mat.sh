set logscale x 2
unset label
#set key above
#set key outside top center horizontal font ",3"
#set autoscale
#set style line 1 linetype 2
set term pdf size 9,4
set output "mat.pdf" 
set xtics (1,2,4,8,16) font "Bold,7" offset 0, graph 0.05
set ytics font "Bold,7"
#set size ratio 0.4
set xlabel "No of Threads" font "Bold,9" offset 0,1.25
set ylabel "Run time (ms)" font "Bold,9" offset 3,0
#set key box
#set key outside horizontal font ",3"
#unset tics
set key at screen 0.6,0.61 font ",7" vertical sample 0.4 spacing 0.3 width 0.5 height 1.0 maxrows 1
#set key off
#set key on center top horizontal center samplen 0.3 spacing 0.5 width 0.3 height 0.3  box lw 1 font ",3"
#set key at 8,16
set multiplot layout 1,2
set yrange [0: 1114461]
#set ylabel "Run time (ms)" font ",3"
set title "Constant Matrix Size\n" font "Bold,7" offset -1.0,-3.5
set size 0.22,0.5
plot "gnuplot_data.dat" using 1:2 title "FATCBST_8" with linespoint lw 2 ps 0.25,\
"gnuplot_data.dat" using 1:3 title "FATCBST_32" with linespoint lw 2 ps 0.25,\
"gnuplot_data.dat" using 1:4 title "FATCBST_128" with linespoint lw 2 ps 0.25,\

set xtics (512, 1024, 2048) font "Bold, 7" offset 0, graph 0.05
set xlabel "Matrix Sizes" font "Bold,9" offset 0,1.25
set ylabel "Run time (ms)" font "Bold,9" offset 3,0
set title "Constant Threads\n" font "Bold,7" offset -1.0,-3.5
set size 0.22,0.5
plot "gnu_constThread.dat" using 1:2 title "FATCBST_8" with linespoint lw 2 ps 0.25,\
"gnu_constThread.dat" using 1:3 title "FATCBST_32" with linespoint lw 2 ps 0.25,\
"gnu_constThread.dat" using 1:4 title "FATCBST_128" with linespoint lw 2 ps 0.25,\
"gnu_constThread.dat" using 1:5 title "DVY" with linespoint lw 2 ps 0.25,\
"gnu_constThread.dat" using 1:6 title "BCCO" with linespoint lw 2 ps 0.25,\

