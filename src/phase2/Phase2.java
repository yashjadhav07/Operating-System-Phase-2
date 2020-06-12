/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

/**
 *
 * @author kiran
 */
import java.io.*;
import java.util.*;

public class Phase2 {
    // Hardware Resources
    static String mainMemory[][] = new String[300][4], instructionRegister[] = new String[4], generalPurposeReg[] = new String[4];
    static String DTApart = "",ErrorMessage = "";
    static int instructionCounter,DTAPointer,TI = 0,PI = 0;
    static int SI, M=0,TTL=0,JID=0,TLL=0,jobStarted = 0,TTC = 0,LLC = 0;
    static int PTR[] = new int [2];
    static boolean  toggle, continueExecution = false;
    
    static File outputFile = new File("C:/Users/kiran/Documents/NetBeansProjects/phase2/output.txt");
    
    static File inputFile = new File("C:/Users/kiran/Documents/NetBeansProjects/phase2/Input1.txt");

    //======================================
    // brief methods
    //======================================
    // print
    static void print(Object args) {
        System.out.println(args);
    }

    // Getting no of frames from number of words
    static int getFrameNos(int words){
        if(10%words == 0) return words/10;
        else return (words/10)+1;
    }

    // main memory display
    static void dispMainMemory(){
        print("\nThe main memory");
        for(int i=0;i<300;i++){
            System.out.print(i+"- ");
            for(int j = 0;j<4;j++){
                System.out.print(mainMemory[i][j]);
            }print("");
        }
    }

    // Displaying file function
    static void fileDisplay(File fileName) {
        try {
            int ch;
            FileReader toDispFile = new FileReader(fileName);

            while ((ch = toDispFile.read()) != -1) {
                System.out.print((char) ch);
            }
            toDispFile.close();
        } catch (Exception ex) {
            System.out.println("Error in Reading file "+ex);
        }
    }

    // Write the user input on file
    static void fileWrite() {
        String temp = "";
        List<String> userProgram = new ArrayList<String>();

        print("Enter your program below");
        Scanner userInput = new Scanner(System.in);

        while (!temp.equals("$END")) {
            String inputBuff = userInput.nextLine();
            userProgram.add(inputBuff);
            temp = inputBuff;
        }

        try {
            FileWriter fw = new FileWriter("input.txt");
            
            for(int i = 0; i<userProgram.size();i++){
                fw.append(userProgram.get(i)+"\n");
            }

            fw.close();
        } catch (Exception ex) {
            print("Error writing file "+ex);
        }
    }

    //loading to main memory
    static void loadToMemory1(String data, int frames){
        int stringIndex = 0;
        int VA = 0, RA = 0, CVA = 1;


        // Loading to main memory and mapping page tables
        //------------------------------------------------------------
        for(int i = 0;i<frames;i++){
            // print("----------------------------" + i+frames);//test...

            // Extracting VA from IR
            try{
                for(int m = 2; m<4;m++) {
                    VA = VA*10 + Integer.parseInt(instructionRegister[m]);
                    // print("va1:- "+VA);//test...
                }

            } catch (NumberFormatException exception){
                // print("VA:- "+VA+"\tCVA:- "+CVA);//test...
                if(VA == CVA){
                    VA+=10;
                }
                CVA = VA;
            }

            //Mapping VA to RA
            if(getRA(VA)<0) mapAddress(VA);

            // Getting The Real Address
            RA = getRA(VA);
            // print("VR:- "+VA+"\tAR:- "+RA);//test...

            // Loading the data to main memory
            for(int l = RA;l<RA+10;l++){
                for(int k=0;k<4;k++){
                    if(stringIndex<data.length()){
                        mainMemory[l][k] = String.valueOf(data.charAt(stringIndex));
                        // System.out.print(mainMemory[l][k]);//test...
                        stringIndex++;
                    } else {
                        k = 4000;
                        l = 20000;
                    }
                }
            } 
            
        }
    }

    //Cleaning Main Memory
    static void cleanMainMemory(){
        for(int i=0;i<300;i++){
            // System.out.print(i+"- "); //Test...
            for(int j = 0;j<4;j++){
                mainMemory[i][j] = null;
                // System.out.print(mainMemory[i][j]); //test...
            }
            // print(""); //Test...
        }
    }

    // Getting Real Address from passed Virtual Address
    static int getRA(int VA){
        int RA = 0;
        int offset = VA%10;
        int base = VA/10;

        // searching for the VA in page table
        for(int i = PTR[0];i<PTR[1];i++){
            int pageTableBase = 0;

            // Extracts VAs from page table
            for(int j = 0;j<2;j++){
                pageTableBase = pageTableBase*10+Integer.parseInt(mainMemory[i][j]);
            }

            // Matching page table VA to VA which program wants
            if(base == pageTableBase){
                // In case they match we will generate the RA as follow
                // To do that we get base of RA related to VA form page table
                for(int j = 2;j<4;j++){
                    RA = RA*10+Integer.parseInt(mainMemory[i][j]);
                }

                RA = RA*10+offset;
                return RA;
            }

        }

        // Eventually return null in case virtual address doesn't match with any of the VRs in page table
        return -1;
    }

    //======================================
    // Phase Two methods
    //======================================
    // Mapping VR to AR
    static void mapAddress(int VA){
        // Searching for hole where we can place our page
        VA = (VA/10);
        for(int j = 0;j<27;j++){ // we just search in 27 blocks (frames) because last tree ones are just allocated for page table
            try{
                // The following checks and if check is successful then it means that the block has an item and it is full
                // Otherwise this check will cause a null exception which means that the block is a hole actually 
                if(mainMemory[j*10][0].equals("arg0"));

                // print("allocated");//test...
            } catch (Exception e){
                // print("not allocated");//test...
                
                // Updating Page Table
                int RA = j;

                // print("VA:- "+VA);//test...
                // print(PTR[1]);//test...

                // Mapping page to frame
                mainMemory[PTR[1]][0] = String.valueOf(VA/10);
                mainMemory[PTR[1]][1] = String.valueOf(VA%10);
                mainMemory[PTR[1]][2] = String.valueOf(RA/10);
                mainMemory[PTR[1]][3] = String.valueOf(RA%10);

                // Updating PTR 
                PTR[1]++;  
                j= 456;              
            }
        }
    }

    //Read function
    static void read(){
        //Re_initializing the SI interruption code
        SI = 0;

        //Read next (data) card from input file in memory locations IR [3,4] through IR [3,4] +9
        instructionRegister[3] = "0";
        int memoryAddress = 0;
        for(int i =2;i<4;i++) memoryAddress = memoryAddress*10 + Integer.parseInt(String.valueOf(instructionRegister[i])); //Getting the Address of the main memory to put data card in.
        M = memoryAddress;
        
        // print(memoryAddress);//test...
        

        // Catching out of Data Error 
        if(DTApart.length() == 0){
            // print("out of data"); //test..
            terminate(1);
        }

        //Loading into the main memory
        //-----------------------------
        String dataCardHolder[] = DTApart.split("#");
        if(DTAPointer<dataCardHolder.length){
            loadToMemory1(dataCardHolder[DTAPointer], 1);
            DTAPointer++;
        }

        // print(DTAPointer); //test...
        // for(String i : dataCardHolder) System.out.print(i + ", "); //test...
        // print(dataCardHolder.length);//test...
        // print("");//test...
    }

    //Write function
    static void write(){
        //Write one block (10 words of memory) from memory locations IR [3,4] through IR [3,4] + 9 to output file
        int memoryAddress = 0;

        //Re_initializing the SI interruption code
        SI = 0;

        // print("Write() SI = "+SI+", TI = "+TI+", PI = "+PI);//test...
        // print("Write() IR:- "+instructionRegister[0]+instructionRegister[1]+instructionRegister[2]+instructionRegister[3]);//test...
        
        // Extracting VA from IR
        for(int i =2;i<4;i++) memoryAddress = memoryAddress*10 + Integer.parseInt(String.valueOf(instructionRegister[i]));
        // print("I will start writing the following block: -" + memoryAddress); //Test...

        // Getting the RA from given VA
        memoryAddress = getRA(memoryAddress);

        try{
            // Catching Line Limit Exceed
            if(LLC >= TLL){
                // print("Caught LLC Error"); //test...
                terminate(2);
                continueExecution = false;
            } else {
                String toBeWritten = "";
                FileWriter wf = new FileWriter(outputFile,true);
                PrintWriter printWF = new PrintWriter(wf);
                inputFile.setWritable(true);
    
                //Going through the main memory
                for (int i = memoryAddress; i < memoryAddress+10; i++) {
                    for (int j = 0; j < 4 && mainMemory[i][j] != null; j++) {
                        toBeWritten = toBeWritten.concat(mainMemory[i][j]);
                        // print("Write()_toBeWritten:- "+toBeWritten);//test...
                    }
                } printWF.append("\n");
    
                //Writing into the file
                printWF.write(toBeWritten);
    
                // Incrementing the LLC
                LLC++;
    
                // Closing the output file and it writer
                printWF.close();
                wf.close();
            }

        } catch (Exception ex){
            print("Write() cannot write into output file:");
            ex.printStackTrace();
        }
    }

    //Terminate function 
    static void terminate(int code){
        // Writes two blank lines to output file and calls the load function
        
        try{
            FileWriter wf = new FileWriter(outputFile,true);
            PrintWriter writer = new PrintWriter(wf);

            // Getting IR contents
            String IR = "";
            // print("terminate() SI = "+SI+", TI = "+TI+", PI = "+PI);//test...
            try {
                // When it come H instruction it is not of length four that cause exception
                for(int i=0;i<4;i++) IR = IR.concat(instructionRegister[i]);
            } catch (Exception e) {
                //TODO: handle exception
            }

            //Let's not let executeUserProgram() to run further
            continueExecution = false;

            // Termination possibilities
            //-------------------------------------------------------
            //code = 0 ---> NO ERROR
            if(code == 0){
                // System.out.println("program out of data.\tterminated(1)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n NO ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 1 ---> out of date
            if(code == 1){
                // System.out.println("program out of data.\tterminated(1)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n OUT OF DATA ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 2 ---> Line Limit Error
            if(code == 2){
                // System.out.println("Line Limit Exceed.\tterminated(2)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  LINE LIMIT EXCEED ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 3 ---> Time Limit Error
            if(code == 3){
                // System.out.println("Time Limit Exceed.\tterminated(3)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  TIME LIMIT EXCEED ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 4 ---> Opcode Error
            if(code == 4){
                // System.out.println("Opcode Error.\tterminated(4)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  OPCODE ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 5 ---> Operand Error
            if(code == 5){
                // System.out.println("Operand Error.\tterminated(5)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  OPERAND ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 6 ---> Page Fault Error
            if(code == 6){
                // System.out.println("Page Fault Error.\tterminate(6)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  INVALID PAGE FAULT\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 7 ---> Time Limit Exceed and Opcode Error
            if(code == 7){
                // System.out.println("Time Limit Exceed and Opcode Error.\tterminate(7)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  TIME LIMIT EXCEED AND OPCODE ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }

            //code = 8 ---> Time Limit Exceed and Operand Error
            if(code == 8){
                // System.out.println("Time Limit Exceed and Operand Error.\tterminate(6)");//test...
                ErrorMessage = "";
                ErrorMessage = ErrorMessage.concat("\nJOB ID\t: "+String.valueOf(JID)+"\n  TIME LIMIT EXCEED AND OPERAND ERROR\n"+
                "IC\t: "+instructionCounter+"\nIR\t: "+IR+"\nTTC\t: "+TTC+
                "\nLLC\t: "+LLC);
            }


            outputFile.setWritable(true);
            writer.append(ErrorMessage+"\n\n");
            writer.close();
            // load();
        } catch (Exception ex) {
            print("Not able to terminate");
            ex.printStackTrace();
        }
    }

    //THE MOS FUNCTION
    static void MOS(){
        // print("MOS_nav");//test...
        // print("TI = "+TI+", SI = "+SI+", PI = "+PI);//test...

        // Having Super permission to do the following (Used to answer the system calls)
        if(TI == 0){
            if(SI == 1){
                read();
            } else if (SI == 2){
                write();
            } else if (SI == 3){
                terminate(0);
            } else if (PI == 1){
                terminate(4);
            } else if (PI == 2){
                terminate(5);
            } else if (PI == 3){
                terminate(6);
            }
        } else if(TI == 2){
            if(SI == 1){
                terminate(3);
            } else if (SI == 2){
                write();
                terminate(3);
            } else if (SI == 3){
                terminate(0);
            } else if (PI == 1){
                terminate(7);
            } else if (PI == 2){
                terminate(8);
            } else if (PI == 3){
                terminate(3);
            }
        }

    }

    //execute user program function
    static void executeUserProgram(){
        // print("------------------------------------executeUserProgram");//test...
        while(continueExecution){
            
            // print("TTC = "+TTC+", TTL = "+TTL);//test...

            if(TTC >= TTL){
                // print("Time Limit Exceed Error caught");//test...
                // terminate(3); //terminate
                if(TI == 2){
                    SI = 1;
                    MOS();
                    continue;
                }
                TI = 2;
            }

            for(int i = 0; i<4;i++) instructionRegister[i] = mainMemory[instructionCounter][i]; //loading to instructions to Instruction Register
            instructionCounter++;

            //Examining the instruction
            String instruction = "";
            int memoryAddress = 0;
            for (int i = 0; i < 2 && instructionRegister[i] != null; i++) instruction = instruction.concat(instructionRegister[i]); //getting the instruction itself
            try {
                for (int i = 2; i < 4 && instructionRegister[i] != null; i++) memoryAddress = (memoryAddress*10) + Integer.parseInt(instructionRegister[i]); // getting the main memory reference Address
            } catch (Exception e) {
                // Catching Operand Error
                // print("Operand Error caught the operand: "+ instructionRegister[2]+instructionRegister[3]);//test...
                // terminate(5); //test...
                PI = 2;
                MOS();
                continueExecution = false;
                continue;
            }                
            // print(memoryAddress + "\t" + instruction); //Test...

            // Updating memoryAddress with getRA() from VA to RA
            // print("VA:- "+memoryAddress+"\tRA:- "+getRA(memoryAddress));//test...
            boolean pageFault = false;
            if(getRA(memoryAddress) >= 0){
                memoryAddress = getRA(memoryAddress);
            } else {
                pageFault = true;
            }

            // Incrementing TTC
            if(TTC<TTL) TTC++;

            // Executing the instructions
            if(instruction.equals("LR")){
                // print("LR_nav");//test...
                // Loading to general purpose Register
                if(!pageFault){
                    try{// We put another check maybe there is internal fragmentation
                        if(mainMemory[memoryAddress][0].equals("arg0"));
                        
                        for(int i = 0; i<4;i++) {
                            generalPurposeReg[i] = mainMemory[memoryAddress][i];
                        } 
                    } catch (Exception exception){
                        // terminate(6);//test...
                        PI = 3;
                        MOS();
                        continueExecution = false;
                    }
                } else {
                    // terminate(6);//test...
                    PI = 3;
                    MOS();
                    continueExecution = false;
                }
                
            } else if(instruction.equals("SR")){
                // Storing General purpose Register to main memory
                String strToBeLoaded = "";
                try {
                    for(int i = 0; i<4;i++)  strToBeLoaded = strToBeLoaded.concat(generalPurposeReg[i]);
                } catch (Exception e) {
                    //TODO: handle exception
                }
                
                // print("Execute User program_SR strToBeLoaded:- "+strToBeLoaded);//test...
                loadToMemory1(strToBeLoaded, 1);

            } else if (instruction.equals("CR")){
                // print("executeUserProgram_CR");//test...
                // Comparing register content to main memory content at specified Address
                toggle = false;
                if(!pageFault) {
                    try{// We put another check maybe there is internal fragmentation
                        if(mainMemory[memoryAddress][0].equals("arg0"));
                        for(int i = 0; i<4;i++){
                            if(generalPurposeReg[i] == mainMemory[memoryAddress][i]) toggle = true;
                            else {
                                toggle = false;
                                break;
                            }
                        }
                    } catch (Exception exception){
                        PI = 3;
                        MOS();
                        continueExecution = false;
                        // terminate(6);//test...
                    }
                } else {
                    // terminate(6);//test...
                    PI = 3;
                    MOS();
                    continueExecution = false;
                }
                
            } else if (instruction.equals("BT")){
                //Jump to specified main memory address if toggler is true
                if(toggle) instructionCounter = memoryAddress;
            } else if (instruction.equals("GD")){
                // Calling the read operation through MOS function
                SI = 1; 
                MOS();
            } else if (instruction.equals("PD")) {
                // print("PD_nav");//test...
                //Calling the write function using MOS function
                if(!pageFault){
                    SI = 2; 
                    MOS();
                } else {
                    // terminate(6);//test...
                    // Invalid Page fault catching
                    PI = 3;
                    MOS();
                    continueExecution = false;
                    continue;
                }
                
            } else if (instruction.matches("H")){
                // Termination of program through the MOS function
                // print("continueExecution"); //test...
                SI = 3; 
                MOS();
                continueExecution = false;
            } else {
                // Catching the Opcode Error
                // print("Opcode Error detected the written: "+instruction);//test...

                // terminate(4);//test...
                PI = 1;
                MOS();
                continueExecution = false;
            }
            
            // print("-----------");//test...
        }
    }
    
    //Start Execution function
    static void startExecution(){
        instructionCounter = 0;

        //calling executeUserProgram
        executeUserProgram();
    }

    //LOAD function
    static void load(){
        try{
            String loadBuffer = "";
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader reader = new BufferedReader(fileReader);
            
            // Reading the lines of the input file.
            loadBuffer = reader.readLine();
            while(loadBuffer != null){
                // print(loadBuffer); //test...
                // print(loadBuffer.substring(0, 4)); //test...
                
                // In case the first four letters are $AMJ
                //-----------------------------------------------------------
                if(loadBuffer.substring(0, 4).equals("$AMJ")){
                    // print("Hanging out with AMJ"); //Test....

                    //Doing all needed for Start part
                    TTL = 0;
                    TLL = 0;
                    JID = 0;
                    M = 0;

                    // Reinitialized Counters
                    TTC = 0;
                    LLC = 0;

                    // Interruption codes initializing
                    SI = 0;
                    PI = 0;
                    TI = 0;

                    // Cleaning Memory at the first of the job
                    cleanMainMemory();

                    // Cleaning IR 
                    instructionRegister[0] = null;
                    instructionRegister[2] = null;

                    // Initializing the PCB and PTR
                    //------------------------------------------------------------------
                    // We by default start PCB structure from the 27 frame upto 30
                    // Then it will be able to map pages to maximum frames 
                    for(int i = 270,j = 27;i<273;i++,j++){
                        mainMemory[i][0] = String.valueOf(j/10);
                        mainMemory[i][1] = String.valueOf(j%10);
                        mainMemory[i][2] = String.valueOf(j/10);
                        mainMemory[i][3] = String.valueOf(j%10);
                    }
                    
                    // Initializing the PTR
                    PTR[0] = 270;
                    PTR[1] = 273;
                    
                    //Getting the TTL, JID, TLL values
                    //------------------------------------------------------------------------
                    for(int i=4;i<8;i++) JID = JID*10 + Integer.parseInt(String.valueOf(loadBuffer.charAt(i)));
                    for(int i=8;i<12;i++) TTL = TTL*10 + Integer.parseInt(String.valueOf(loadBuffer.charAt(i)));
                    for(int i=12;i<16;i++) TLL = TLL*10 + Integer.parseInt(String.valueOf(loadBuffer.charAt(i)));

                    // print(JID + " " + TTL + " " + TLL);//test...

                    //Loading the Control card to main memory
                    //-------------------------------------------------------
                    loadBuffer = reader.readLine();
                    String controlsToBeLoaded = "";
                    
                    //if first four characters of the the next line is not equal to $DTA the keep loading th line to main memory
                    while(true){
                        controlsToBeLoaded = controlsToBeLoaded.concat(loadBuffer);
                        loadBuffer = reader.readLine();

                        //if the length of the load buffer is less than 4 then it will raise exception which is needed considered 
                        if (loadBuffer.length() >= 4){
                            if(loadBuffer.substring(0, 4).equals("$DTA")) break;
                        }
                    } 
                    
                    // print(getFrameNos(TTL));//test...

                    // Loading the control cards into main memory
                    loadToMemory1(controlsToBeLoaded, getFrameNos(TTL));
            
                    // print(M); //test...
                    // print(controlsToBeLoaded); //test...
                    // print(loadBuffer); //test...
                    
                    //After loading the control cart to main memory we need to take care of the data part.
                    //---------------------------------------------------------------------------------------------
                    DTApart = ""; 
                    DTAPointer = 0; 

                    while(true){
                        loadBuffer = reader.readLine();
                        if(loadBuffer.length()>=4){
                            if(loadBuffer.substring(0, 4).equals("$END")) break;
                        }
                        DTApart = DTApart.concat(loadBuffer+"#");
                    }

                    //calling for startExecution()
                    continueExecution = true;
                    startExecution();

                    // print("data part: "+DTApart); //test...
                    // // print(loadBuffer); //test...
                    // mapAddress(45);//test...
                    // dispMainMemory(); //test...
                    // print(getRA(0));//test...
                    // print(getRA(3));//test...
                    // print(getRA(120));//test...
                    // loadToMemory1("data", 4);
                    // print("Ending the job"); //test...
                }
                loadBuffer = reader.readLine();

            }

            reader.close();
        } catch (Exception exception) {
            print("load(): cannot read the input file:");
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {

        
        fileDisplay(inputFile);
        load();
        fileDisplay(outputFile);
       
    }
}
