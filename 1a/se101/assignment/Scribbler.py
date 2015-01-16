if(1):
	import copy;
	global grid, xMax, yMax, xMin, yMin, angle, irpower, gridsize;
	irpower = 132;
	gridsize = 1;
	grid=[[2]]
	xMax=yMax=xMin=yMin=direction=0
	#Add a colunm at the end (xMax + 1)
	def appendColumn(value):
		global grid, xMax, yMax, xMin, yMin;
		xMax = xMax+1;
		for i in xrange(0, len(grid)):
			grid[i].append(value);
	#Add a column at the front (xMin - 1)
	def insertColumn(value):
		global grid, xMax, yMax, xMin, yMin;
		xMin = xMin - 1;
		for i in xrange(0, len(grid)):
			grid[i].insert(0, value);
	#Add a row at the end (yMax + 1)
	def appendRow(value):
		global grid, xMax, yMax, xMin, yMin;
		yMax = yMax + 1;
		grid.append([0]);
		for i in xrange (0, len(grid[0]) - 1):
			grid[len(grid)-1].append(value);
	#Add a row at the front (yMin - 1)
	def insertRow(value):
		global grid, xMax, yMax, xMin, yMin;
		yMin = yMin - 1;
		grid.insert(0,[0]);
		for i in xrange(0, len(grid[1]) - 1):
			grid[0].append(value);
	#Set the value of a location on the map
	def setMap(x, y, value):
		global grid, xMax, yMax, xMin, yMin;
		while(x > xMax):
			appendColumn(0);
		while(x < xMin):
			insertColumn(0);
		while(y > yMax):
			appendRow(0);
		while(y < yMin):
			insertRow(0);
		grid[y - yMin][x - xMin] = value;
	#Get the value of a location on the map
	def getMap(x, y):
		global grid, xMax, yMax, xMin, yMin;
		while(x > xMax):
			appendColumn(0);
		while(x < xMin):
			insertColumn(0);
		while(y > yMax):
			appendRow(0);
		while(y < yMin):
			insertRow(0);
		return grid[y - yMin][x - xMin];
	#Get closest location with a certain value
	def getClosest(x, y, dir, value):
		global grid, xMax, yMax, xMin, yMin;
		found = 0;
		for i in xrange(0, len(grid)):
			if(found > 0):
				break;
			for j in xrange(0, len(grid[i])):
				if(grid[i][j] == value):
					if((i + xMin == 0 and j + yMin == 0) or (i>0 and grid[i-1][j]== 1) or (i<len(grid)-1 and grid[i+1][j]== 1) or (j>0 and grid[i][j-1]== 1) or (j<len(grid[i])-1 and grid[i][j+1]== 1)):
						found = found + 1;
						break;
					else:
						grid[i][j] = -1;
		if(found == 0):
			return 0;
		
		delta = 1;
		move = 0;
		flip = 1;
		while(1):
			#Check value
			if(x >= xMin and x <= xMax and y >= yMin and y <= yMax):
				if(grid[y - yMin][x - xMin] == value):
					return [x, y];
			#Increment move
			if(dir == 0):
				x = x + 1;
			elif(dir == 1):
				y = y + 1;
			elif(dir == 2):
				x = x - 1;
			elif(dir == 3):
				y = y - 1;
			#Rotate if necessary
			move = move + 1;
			if(move >= delta):
				move = 0;
				dir = (dir + 1) % 4;
				
				if(flip != 0):
					delta = delta + 1;
					
				if(flip == 0):
					flip = 1;
				else:
					flip = 0;
				
		return 0;
	
	def waitMotor():
		while(isReady() != 1):
			time.sleep(0.1);
		time.sleep(0.2);
		while(isReady() != 1):
			time.sleep(0.1);
	
	def turnBy(degrees):
		degrees = (degrees + 360) % 360;
		myro.globvars.robot.setPacket(164, 5, degrees / 256, degrees % 256);
		waitMotor();
	
	def turnTo(degrees):
		global angle;
		turnBy(degrees - angle);
		angle = degrees;
		
	def oldTurnTo(angle):
		angle = (angle + 360) % 360;
		angle = (955 * angle) / 360
		myro.globvars.robot.setPacket(164, 2, angle / 256, angle % 256);
		waitMotor();
		
	def go(x, y, xFrom, yFrom):
		dx = x - xFrom;
		dy = y - yFrom;
		if(abs(dx) < abs(dx)):
			moveX(dx);
			moveY(dx);
		else:
			moveY(dy);
			moveX(dx);
			
	def moveX(dx):
		if(dx == 0):
			return;
		degrees = 0;
		if(dx < 0):
			degrees = 180;
		dx = abs(dx);
		turnTo(degrees);
		while(dx > 0):
			moveGrid();
			dx = dx - 1;
	def moveY(dy):
		if(dy == 0):
			return;
		degrees = 90;
		if(dy < 0):
			degrees = 270;
		dy = abs(dy);
		turnTo(degrees);
		while(dy > 0):
			moveGrid();
			dy = dy - 1;
			
	def moveGrid():
		global gridsize;
		forward(1, gridsize);
		

	# 125-135 seems to be reasonable
	def setIR(power):
		setIRPower(power);
	
	def isReady():
		result = myro.globvars.robot.getPacket(170, 5);
		if(result[0] == 0 and result[1] == 0 and result[2] == 0 and result[4] == 1):
			return 1;
		return 0;
		
	def getAvgObstacle():
		max = 3;
		t = 0;
		leftavg = 0;
		midavg = 0;
		rightavg = 0;
		threshHold = 500;
		while(t < max):
			t = t + 1;
			midavg = midavg + getObstacle("center");
			leftavg = leftavg + getObstacle("left");
			rightavg = rightavg + getObstacle("right");
		
		return [leftavg / max, midavg / max, rightavg / max];
		
	def scan(angles):
		obstacles = getAvgObstacle();
		if(obstacles[1] > 500 or (angles % 90 != 0 and (obstacles[0] > 500 or obstacles[2] > 500))):
			return -1;
		return 2;
	
	def printGrid():
		global grid;
		for i in xrange(0, len(grid)):
			print grid[len(grid) - 1 - i];
			
	def reset():
		goto(0, 0);
		
	def scanAndSet(x, y, degrees):
		if(getMap(x, y) == 0):
			turnTo(degrees);
			result = scan(degrees);
			setMap(x, y, result);
		
		
	def scanAndSetConvert(x, y, angle):
		angle = (angle + 360) % 360;
		
		if(angle == 0):
			return scanAndSet(x + 1, y, angle);
		if(angle == 45):
			return scanAndSet(x + 1, y + 1, angle);
		if(angle == 90):
			return scanAndSet(x, y + 1, angle);
		if(angle == 135):
			return scanAndSet(x - 1, y + 1, angle);
		if(angle == 180):
			return scanAndSet(x - 1, y, angle);
		if(angle == 225):
			return scanAndSet(x - 1, y - 1, angle);
		if(angle == 270):
			return scanAndSet(x, y - 1, angle);
		if(angle == 315):
			return scanAndSet(x + 1, y - 1, angle);
		
	def doWork():
		global grid, xMax, yMax, xMin, yMin, angle;
		# 0 is unexplored, 1 is free & explored, 2 is unexplored free, -1 is occupied
		setIR(irpower);
		#myro.globvars.robot.setPacket(161, 1, 0, 10);
		
		grid=[[2]];
		xMax=yMax=xMin=yMin=direction=0;
		
		dir = 0;
		x = y = 0;
		angle = 0;
		flip = 1;
		
		while(1):
			# find closest (unexplored free), if not found then exit
			dir = 0;
			if(angle == 270):
				dir = 1;
			if(angle == 0):
				dir = 2;
			if(angle == 90):
				dir = 3;
			location = getClosest(x, y, dir, 2);
			if(location == 0):
				break;
			# setRobotDirection(
			
			# move to that location
			if(x != location[0] or y != location[1]):
				print("Moving to", location[0], location[1]);
				go(location[0], location[1], x, y);
				x = location[0];
				y = location[1];
			
			setMap(x, y, 1);
			
			print("Scanning");
			
			# scan all 0's next to the location and record data
			i = 0;
			while(i < 8):
				scanAndSetConvert(x, y, flip * 45 * i);
				i = i + 1;
			
			flip = -flip;
			printGrid();
			moveRobot(x - xMin, y - yMin);
			updateRender(grid);
			render();
			
		print("Done");
		moveRobot(x - xMin, y - yMin);
		updateRender(grid);
		renderAll();
	
	
		#key settings
	BORDER_WIDTH = 15 # border width when using renderAll()
	GRID_SIZE = 30 # square size
	WINDOW_SIZE = 15 # size of the realtime tracking window (n x n grids)
	CENTER_BIAS = 2 # gives a bias to the camera center
	
	#constants
	HALF_EXTENSION = (WINDOW_SIZE - 1) / 2
	OBS = makePicture(GRID_SIZE, GRID_SIZE, makeColor(50, 50, 50)) #state -1
	FREE = makePicture(GRID_SIZE, GRID_SIZE, makeColor(200, 200, 200)) #state 2
	PATH = makePicture(GRID_SIZE, GRID_SIZE, makeColor(150, 150, 150)) #state 1
	UNK = 0 #state 0
	
	#resources
	SCB_RIGHT = makePicture("C:\Users\Administrator\Desktop\ImageRenderer\Res\ScribblerRight.png")
	SCB_UP = makePicture("C:\Users\Administrator\Desktop\ImageRenderer\Res\ScribblerUp.png")
	SCB_LEFT = makePicture("C:\Users\Administrator\Desktop\ImageRenderer\Res\ScribblerLeft.png")
	SCB_DOWN = makePicture("C:\Users\Administrator\Desktop\ImageRenderer\Res\ScribblerDown.png")
	
	#variables and initial values
	robotPositionX = 2
	robotPositionY = 4
	robotDirection = 3 # 0 = right, 1 = up, 2 = left, 3 = down
	cameraPositionX = 0
	cameraPositionY = 0
	currentAnchorX = 0
	currentAnchorY = 0
	anchorChanged = 0
	
	#temps
	images = [[]]
	robotImage = 0
	winR = 0
	win = 0
	
	#coordinate system: +x = Right, +y = Down
	
	#Reminder: must use updateRender on the appropriate grid every time after moveRobot to avoid ALREADY_DRAWN error
	def moveRobot(x, y): # move robot to pos x, y (must use for proper results!)
		global robotPositionX
		global robotPositionY
		global robotDirection
		if(x > robotPositionX):
			robotDirection = 0
		elif(x < robotPositionX):
			robotDirection = 2
		elif(y < robotPositionY):
			robotDirection = 1
		elif(y > robotPositionY):
			robotDirection = 3
		robotPositionX = x
		robotPositionY = y
	
	def setRobotDirection(x): #sets the robot's direction; only useful when adjusting robot's direction when it is not moving
		global robotDirection
		if(0 <= x & x <= 3):
			robotDirection = x
			patchRobotImage()
			if not(winR.isClosed()):
				robotImage.draw(winR)
		else:
			print("0 = right, 1 = up, 2 = left, 3 = down\n")
			
	
	def updateRender(grid): # this keeps the renderer update with the numerical grid
		global images
		if((len(grid[0]) == len(images[0])) and (len(grid) == len(images))): # the size of the array hasn't changed, doesn't need to update all
			for i in range(currentAnchorY + robotPositionY - 1, currentAnchorY + robotPositionY + 2):
				for j in range(currentAnchorX + robotPositionX - 1, currentAnchorX + robotPositionX + 2):
					if(grid[i][j] == 0):
						images[i][j] = UNK
					elif(grid[i][j] == 1):
						images[i][j] = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + j * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + i * GRID_SIZE), PATH)
					elif(grid[i][j] == -1):
						images[i][j] = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + j * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + i * GRID_SIZE), OBS)
					elif(grid[i][j] == 2):
						images[i][j] = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + j * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + i * GRID_SIZE), FREE)
		else:
			images = copy.deepcopy(grid) #copies numerical grid
			for rowIndex, row in enumerate(grid):
				for columnIndex, entry in enumerate(row):
					if(entry == 0):
						images[rowIndex][columnIndex] = UNK
					elif(entry == 1):
						images[rowIndex][columnIndex] = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + columnIndex * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + rowIndex * GRID_SIZE), PATH)
					elif(entry == -1):
						images[rowIndex][columnIndex] = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + columnIndex * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + rowIndex * GRID_SIZE), OBS)
					elif(entry == 2):
						images[rowIndex][columnIndex] = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + columnIndex * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + rowIndex * GRID_SIZE), FREE)
		patchRobotImage()
	
	def patchRobotImage(): # renders Robot Image; not for individual use!
		global robotImage
		if(robotDirection == 0):
			robotImage = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorX + robotPositionX) * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorY + robotPositionY) * GRID_SIZE), SCB_RIGHT)
		elif(robotDirection == 1):
			robotImage = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorX + robotPositionX) * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorY + robotPositionY) * GRID_SIZE), SCB_UP)
		elif(robotDirection == 2):
			robotImage = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorX + robotPositionX) * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorY + robotPositionY) * GRID_SIZE), SCB_LEFT)
		elif(robotDirection == 3):
			robotImage = Image(Point(BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorX + robotPositionX) * GRID_SIZE, BORDER_WIDTH + GRID_SIZE/2 + (currentAnchorY + robotPositionY) * GRID_SIZE), SCB_DOWN)
	
	def render(): #completely renders all with the robot displayed and centered
		global winR
		global anchorChanged
		if(win):
			win.close()
		if(winR):
			winR.close()
		winR = GraphWin("Realtime Tracking", GRID_SIZE * WINDOW_SIZE, GRID_SIZE * WINDOW_SIZE)
		setCamera(currentAnchorX + robotPositionX, currentAnchorY + robotPositionY)
		#for i in range(currentAnchorY + robotPositionY - HALF_EXTENSION, currentAnchorY + robotPositionY + HALF_EXTENSION + 1):
		#	for j in range(currentAnchorX + robotPositionX - HALF_EXTENSION, currentAnchorX + robotPositionX + HALF_EXTENSION + 2):
		#		if(i >= 0 & i <	len(images) & j >= 0 & j < len(images[0])):
		#			print(i,j)
		#			if(images[i][j]):
		#				images[i][j].draw(winR)
		height = len(images)
		width = len(images[0])
		for i in range(0, height):
			for j in range(0, width):
				if(images[i][j]):
					images[i][j].draw(winR)
		robotImage.draw(winR)
		anchorChanged = 0
	
	def renderUpdate(): #only redraws the nearing squares of the robot
		global anchorChanged
		if(anchorChanged): # redraw the whole image if anchor is changed.. This is a compromise and no better solution is currently found. Avoid using setAnchor() as much as possible!
			height = len(images)
			width = len(images[0])
			for i in range(0, height):
				for j in range(0, width):
					if(images[i][j]):
						images[i][j].draw(winR)
		else:
			for i in range(currentAnchorY + robotPositionY - 1, currentAnchorY + robotPositionY + 2):
				for j in range(currentAnchorX + robotPositionX - 1, currentAnchorX + robotPositionX + 2):
					if(images[i][j]):
						images[i][j].draw(winR)
		robotImage.draw(winR)
		anchorChanged = 0
		
	def renderAll(): #renders the whole grid map without border limitations and robot display
		global win
		if(winR):
			winR.close()
		if(win):
			win.close()
		height = len(images)
		width = len(images[0])
		
		win = GraphWin("View Full Map", GRID_SIZE * width + 2 * BORDER_WIDTH, GRID_SIZE * height + 2 * BORDER_WIDTH)
		win.setCoords(0, 0, GRID_SIZE * width + 2 * BORDER_WIDTH, GRID_SIZE * height + 2 * BORDER_WIDTH)
		
		for i in range(0, height):
			for j in range(0, width):
				if(images[i][j]):
					images[i][j].draw(win)
		
	def setCamera(x, y): #sets the position of the camera in Realtime view (requires re-render to reflect)
		global cameraPositionX
		global cameraPositionY
		cameraPositionX = x
		cameraPositionY = y
		winR.setCoords(-(HALF_EXTENSION - x) * GRID_SIZE / CENTER_BIAS, - (HALF_EXTENSION - y) * GRID_SIZE / CENTER_BIAS, GRID_SIZE * WINDOW_SIZE - (HALF_EXTENSION - x) * GRID_SIZE / CENTER_BIAS, GRID_SIZE * WINDOW_SIZE - (HALF_EXTENSION - y) * GRID_SIZE / CENTER_BIAS)	
	
	# AVOID this as much as possible #
	def setAnchor(x, y): #sets where coordinate 0, 0 is in the grid (Only need to do this when the array is expanded upward or to the left)
		global currentAnchorX
		global currentAnchorY
		global anchorChanged
		currentAnchorX = x
		currentAnchorY = y
		anchorChanged = 1