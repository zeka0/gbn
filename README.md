#Author - Ether Wei

###Introduction
This is a Java datagram socket based Go-Back-N simulation program.
It implements the FSM based GBN internet protocol.

###Class Notes

####ByteConvertor
This class is used to convert bytes into short or int.
Or convert short to int to bytes.

####InetLogger
A file based logger that dedicated to log information during the procession of GBN protocol.

####FlawedSocket
A flawed socket is used to monitor the actual envrionment in our daily lifes.
It simulates __package lost__ and __package corruption__.
By default, 20% of the packages are lost and 20% of packages are corrupted.

####GBN.sender
This package contains classes on the sender side of the protocol.
It requires __3__ threads to run.
Namely, __timer thread__ which controls the timing,  __sender thread__ which is the main thread, __receiver thread__ which receives all the incoming packages through the internet.

####GBN.receiver
This package contains classes on the receiver side of the protocol.
It requires __1__ threads to run.

###After notes
You can run __Main.java__ to check out.
Be sure to modify the data paths in Main.java before running the example.
Also, if you find the program is a little bit too slow, you may comment out all the logging statements in source files.
