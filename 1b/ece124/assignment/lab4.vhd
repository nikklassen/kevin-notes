library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.std_logic_unsigned.all;

entity SevenSegment is port (

	datain		:	in	std_logic_vector(3 downto 0); 
	blanking	:	in	std_logic;

	segmentsout	:	out	std_logic_vector(6 downto 0)
);

end SevenSegment;

architecture Behavioral of SevenSegment is

begin

	with blanking & dataIn select
		segmentsOut(6 downto 0) <=	"1000000" when "00000",
									"1111001" when "00001",
									"0100100" when "00010",
									"0110000" when "00011",
									"0011001" when "00100",
									"0010010" when "00101",
									"0000010" when "00110",
									"1111000" when "00111",
									"0000000" when "01000",
									"0010000" when "01001",
									"0001000" when "01010",
									"0000011" when "01011",
									"0100111" when "01100",
									"0100001" when "01101",
									"0000110" when "01110",
									"0001110" when "01111",
									"1111111" when others; 

end Behavioral;

entity Lab4 is port (

	clock_50	:	in	std_logic;
	sw			:	in	std_logic_vector(17 downto 14);

	ledr		:	out	std_logic_vector(11 downto 0);
	ledg		:	out std_logic_vector( 8 downto 0);
	hex0, hex1, hex2, hex3, hex4, hex5, hex6, hex7	:	out std_logic_vector( 6 downto 0)
);

end Lab4;

architecture TrafficControl of Lab4 is

	component SevenSegment port (

		dataIn		:	in		std_logic_vector(3 downto 0);
		blanking	:	in		std_logic;

		segmentsOut	:	out	std_logic_vector(6 downto 0)
	);

	end component;


	constant CLK_size:		integer := 25;

	signal TenHzModCLK:		std_logic;
	signal OneHzModCLK:		std_logic;
	signal LEDOn:			std_logic;

	signal modCounter:		unsigned(CLK_size-1 downto 0) := to_unsigned(0,CLK_size);
	signal OneHzCounter:	unsigned(3 downto 0) := to_unsigned(0,4);
	signal ModTerminal:		unsigned(CLK_size-1 downto 0) := to_unsigned(0,CLK_size);

	type STATES is (STATE0, STATE1, STATE2, STATE3, STATE4, STATE5);
	signal STATE, next_mainSTATE:	STATES;

	signal StateNumber:		std_logic_vector(3 downto 0);
	signal StateCounter:	unsigned(3 downto 0);
	signal NSCounter:		unsigned(3 downto 0);
	signal EWCounter:		unsigned(3 downto 0);

	signal OpMode:			std_logic;
	signal DefaultSide:		std_logic;

	signal NSSensor:		std_logic;
	signal EWSensor:		std_logic;

	signal NSDisplay:			std_logic;
	signal EWDisplay:			std_logic;

begin

	ModTerminal <= "0001001100010010110011111";
	ledr(10 downto 1) <= "0000000000";
	ledg( 6 downto 2) <= "00000";
	
	OpMode <= sw(17);
	DefaultSide <= sw(16);
	NSSensor <= sw(15);
	EWSensor <= sw(14);


	CLK: PROCESS(clock_50)
	begin
		if (rising_edge(clock_50)) then
			if (modCounter = ModTerminal) then
				TenHzModCLK <= not TenHzModCLK;
				modCounter <= to_unsigned(0,CLK_size);
						
				if (OneHzCounter = "1001") then
					OneHzModCLK <= not OneHzModCLK;
					OneHzCounter <= to_unsigned(0,4);
				else
					OneHzCounter <= OneHzCounter + 1;
				end if;

				LEDOn <= not LEDOn;
				if (STATE = STATE0) then
					-- ns
					ledg(8) <= LEDOn;
					ledr(11) <= '0';
					-- ew
					ledr(0) <= '1';
					ledg(7) <= '0';
				end if;
				if (STATE = STATE1) then
					-- ns
					ledr(11) <= '0';
					ledg(8) <= '1';
					-- ew
					ledr(0) <= '1';
					ledg(7) <= '0';
				end if;
				if (STATE = STATE2) then
					-- ns
					ledr(11) <= LEDOn;
					ledg(8) <= '0';
					-- ew
					ledr(0) <= '1';
					ledg(7) <= '0';
				end if;
				if (STATE = STATE3) then
					-- ns
					ledr(11) <= '1';
					ledg(8) <= '0';
					-- ew
					ledg(7) <= LEDOn;
					ledr(0) <= '0';
				end if;
				if (STATE = STATE4) then
					-- ns
					ledr(11) <= '1';
					ledg(8) <= '0';
					-- ew
					ledg(7) <= '1';
					ledr(0) <= '0';
				end if;
				if (STATE = STATE5) then
					-- ns
					ledr(11) <= '1';
					ledg(8) <= '0';
					-- ew
					ledg(7) <= '0';
					ledr(0) <= LEDOn;
				end if;
				
			else
				modCounter <= modCounter + 1;
			end if;
		end if;
	end PROCESS;

	ledg(0) <= TenHzModCLK;
	ledg(1) <= OneHzModCLK;


	FSM: PROCESS(STATE, DefaultSide, NSSensor, EWSensor, OpMode)
	begin
		case STATE is
			when STATE0 =>
				StateNumber <= "0000";
				next_mainSTATE <= STATE1;
			when STATE1 =>
				StateNumber <= "0001";
				next_mainSTATE <= STATE2;
			when STATE2 =>
				if (OpMode = '1' and DefaultSide = '0' and EWSensor = '0') then
					next_mainSTATE <= STATE0;
				else
					next_mainSTATE <= STATE3;
				end if;
				StateNumber <= "0010";
			when STATE3 =>
				StateNumber <= "0011";
				next_mainSTATE <= STATE4;
			when STATE4 =>
				StateNumber <= "0100";
				next_mainSTATE <= STATE5;
			when STATE5 =>
				if (OpMode = '1' and DefaultSide = '1' and NSSensor = '0') then
					next_mainSTATE <= STATE3;
				else
					next_mainSTATE <= STATE0;
				end if;
				StateNumber <= "0101";
		end case;
	end PROCESS;

	SEQLogic: PROCESS(OneHzModCLK, STATE)
	begin
		if (rising_edge(OneHzModCLK)) then
			if (StateCounter = "0001" or StateCounter = "0101" or StateCounter = "0111" or StateCounter = "1001" or StateCounter = "1101" or StateCounter = "1111") then
				STATE <= next_mainSTATE;
			end if;

			if (StateCounter = "0101") then
				EWCounter <= "1010";
			end if;
			if (StateCounter = "1101") then
				NSCounter <= "1010";
			end if;

			if (EWCounter /= "0000") then
				EWCounter <= EWCounter - 1;
			end if;
			if (NSCounter /= "0000") then
				NSCounter <= NSCounter - 1;
			end if;

			StateCounter <= StateCounter + 1;

			if (NSSensor = '0' or (DefaultSide = '0' and EWSensor = '0') or NSCounter = "0000" or NSCounter = "0001" or NSCounter = "1010") then
				NSDisplay <= '1'; -- off
			else
				NSDisplay <= '0'; -- on
			end if;
			if (EWSensor = '0' or (DefaultSide = '1' and NSSensor = '0') or EWCounter = "0000" or EWCounter = "0001" or EWCounter = "1010") then
				EWDisplay <= '1'; -- off
			else
				EWDisplay <= '0'; -- on
			end if;
		end if;
	end PROCESS;


	D7S0: SevenSegment port map(StateNumber,						'0', hex0 );
	D7S2: SevenSegment port map(std_logic_vector(StateCounter),		'0', hex2 );

	D7S4: SevenSegment port map(std_logic_vector(NSCounter),		NSDisplay, hex4 );
	D7S6: SevenSegment port map(std_logic_vector(EWCounter),		EWDisplay, hex6 );
	
	
	D7S1: SevenSegment port map("0000", '1', hex1 );
	D7S3: SevenSegment port map("0000", '1', hex3 );
	D7S5: SevenSegment port map("0000", '1', hex5 );
	D7S7: SevenSegment port map("0000", '1', hex7 );

end TrafficControl;