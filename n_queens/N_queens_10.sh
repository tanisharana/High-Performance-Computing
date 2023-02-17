set logscale x 2
unset label
#set key above
#set key outside top center horizontal font ",3"
#set autoscale
#set style line 1 linetype 2
set term pdf size 9,4
set output "N_queens_10.pdf" 
set xtics (1,2,4,6,8,10,12,14,16) font "Bold,7" offset 0, graph 0.05
set ytics font "Bold,7"
#set size ratio 0.9
set xlabel "No of Threads" font "Bold,9" offset 0,1.25
set ylabel "Run time (s)" font "Bold,9" offset 3,0
#set key box
#set key outside horizontal font ",3"
#unset tics
set key at screen 0.6,0.61 font ",7" vertical sample 0.4 spacing 0.3 width 0.5 height 1.0 maxrows 1
#set key off
#set key on center top horizontal center samplen 0.3 spacing 0.5 width 0.3 height 0.3  box lw 1 font ",3"
#set key at 8,16
set multiplot layout 1,2
set yrange [0.006: 0.01]
#set ylabel "Run time (ms)" font ",3"
set title "N queens, 10 Boardsize" font "Bold,10" offset -1.0,-3.5
set size 0.4,0.7
plot "N_queens.dat" using 1:5 notitle with linespoint lw 2 ps 0.25,\


