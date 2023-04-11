import matplotlib.pyplot as plt

# Read the data from the txt file
data = []
with open('/content/input.txt', 'r') as file:
    for line in file:
        data.append(list(map(int, line.split())))

# Extract the x and y values for each line
x1 = [row[0] for row in data]
x2 = [row[1] for row in data]
y1 = [row[2] for row in data]
y2 = [row[3] for row in data]

# Create the plot
plt.plot(x1, y1, label='Thread 1')
plt.plot(x2, y2, label='Thread 2')

# Add labels and legend
plt.xlabel('Workload')
plt.ylabel('Time (ns)')
plt.title('Execution time vs Workload')
plt.legend()

# Display the plot
plt.show()
