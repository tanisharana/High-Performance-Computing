set logscale x 2
unset label
#set key above
#set key outside top center horizontal font ",3"
#set autoscale
#set style line 1 linetype 2
set term pdf size 10,4
set output "merge_sort_double.pdf" 
set xtics (1,2,4,6,8,10,12,14,16) font "Bold,7" offset 0, graph 0.05
set ytics font "Bold,7"
#set size ratio 0.5
set xlabel "No of Threads" font "Bold,9" offset 0,1.25
set ylabel "Run time (s)" font "Bold,9" offset 3,0
#set key box
#set key outside horizontal font ",3"
#unset tics
set key at screen 0.6,0.61 font ",7" vertical sample 0.4 spacing 0.3 width 0.7 height 1.0 maxrows 1
#set key off
#set key on center top horizontal center samplen 0.3 spacing 0.5 width 0.7 height 0.2  box lw 1 font ",3"
#set key at 8,16
set multiplot layout 1,1
set yrange [0: 9.00]
#set ylabel "Run time (s)" font ",4"
set title "Merge sort double" font "Bold,10" offset -1.0,-3.5
set size 0.4,0.6
plot "mergesort.dat" using 1:4 notitle with linespoint lw 2 ps 0.5,\


