
clear all;

cores=[1 2 4 8 16 20];
coresF=[1 2 4 8 12 16 20];
timeJava=[13610634 6954175 3500408 1956986 1584536 1961205]/1000000;
timeFortran=[5.46 4.39 2.22 1.29 0.95 1.33 1.02];
ap1 = timeJava(1);
ap2 = timeFortran(1);
speedupJava = zeros(1,length(timeJava));
speedupF = zeros(1,length(timeFortran));

for i=1:length(timeJava)
    speedupJava(i) = ap1./timeJava(i);
end

for i=1:length(timeFortran)
    speedupF(i) = ap2./timeFortran(i);
end

f1 = figure('Name','speedup');
hold on;
xlabel('Threads');
ylabel('Speedup');
title('Speedup // Transpose matrix 32768 x 32768');
plot(cores,speedupJava,'--r');
plot(coresF,speedupF,'--b');
legend('Java','Fortran');

f2 = figure('Name','time');
hold on;
xlabel('Threads');
ylabel('Time(s)');
title('Time to solution // Transpose matrix 32768 x 32768');
plot(cores,timeJava,'--r');
plot(coresF,timeFortran,'--b');
legend('Java','Fortran');
