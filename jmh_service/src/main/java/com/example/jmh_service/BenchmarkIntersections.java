package com.example.jmh_service;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xmx2G", "-Xms2G"})
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class BenchmarkIntersections {

    @Param("100000")
    private int N;

    private List<Integer> LIST_FOR_TESTING_1;
    private List<Integer> LIST_FOR_TESTING_2;
    private Random random = new Random();

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkIntersections.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();

    }

    @Setup
    public void setup() {
        LIST_FOR_TESTING_1 = genAscendingList(4);
        LIST_FOR_TESTING_2 = genAscendingList(4);
    }

    public void smartIntersection(Blackhole bh){
        intersectSortedList(LIST_FOR_TESTING_1, LIST_FOR_TESTING_2);
    }

    @Benchmark
    public void naiveIntersection() {
        intersect(LIST_FOR_TESTING_1, LIST_FOR_TESTING_2);
    }

    private List<Integer> intersectSortedList(List<Integer> l1, List<Integer> l2) {

        int i1 = 0;
        int i2 = 0;

        List<Integer> resultList = new ArrayList<>();

        while (i1 < l1.size() && i2 < l2.size()) {

            int obj1 = l1.get(i1);
            int obj2 = l2.get(i2);

            if (obj1 == obj2) {
                resultList.add(obj1);
                i1++;
                i2++;
            } else if (obj1 > obj2) {
                i2++;
            } else {
                i1++;
            }
        }

        return resultList;
    }

    private List<Integer> genAscendingList(int step) {

        List<Integer> l = new ArrayList<>();
        int curValue = 0;

        for (int i = 0; i < N; i++) {
            int delta = random.nextInt() % step;
            curValue += delta;
            l.add(curValue);
        }

        return l;
    }

    private <T> List<T> intersect(List<T> l1, List<T> result) {

//        List<T> result = new ArrayList<>(l2);
        result.retainAll(l1);
        return result;

    }

}
