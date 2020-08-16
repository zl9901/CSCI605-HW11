/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation, and proper error handling, might not be present in
 * this sample code.
 */
/*
 * THIS CODE WAS MODIFIED BY hpb
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class LE {

    //The number of characters that may be read.
    private static final int READ_AHEAD_LIMIT = 100_000_000;

    //The pattern for splitting strings by non word characters to get words.
    private static final Pattern nonWordPattern = Pattern.compile("\\W");

    /**
     * The main method for the LE program. Run the program with an empty
     * argument list to see possible arguments.
     *
     * @param args the argument list for LE
     * @throws java.io.IOException If an input exception occurred.
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            usage();
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(args[0]))) {
            reader.mark(READ_AHEAD_LIMIT);
            /*
             * Statistics can be gathered in four passes using a built-in API.
             * The method demonstrates how separate operations can be
             * implemented using a built-in API.
             */
            collectInFourPasses(reader);
            /*
             * Usage of several passes to collect data is not the best way.
             * Statistics can be gathered by a custom collector in one pass.
             */
            reader.reset();
            collectInOnePass(reader);
        } catch (FileNotFoundException e) {
            usage();
            System.err.println(e);
        }
    }

    private static void collectInFourPasses(BufferedReader reader)
            throws IOException {
        /*
         * Input is read as a stream of lines by lines().
         * Every line is turned into a stream of chars by the flatMapToInt(...)
         * method.
         * Length of the stream is counted by count().
         */
        System.out.println("Character count = "
                + reader.lines().flatMapToInt(String::chars).count());
        /*
         * Input is read as a stream of lines by lines().
         * Every line is split by nonWordPattern into words by flatMap(...)
         * method.
         * Empty lines are removed by the filter(...) method.
         * Length of the stream is counted by count().
         */
        reader.reset();
        System.out.println("Word count = "
                + reader.lines()
                .flatMap(nonWordPattern::splitAsStream)
                .filter(str -> !str.isEmpty()).count());

        reader.reset();
        System.out.println("Newline count = " + reader.lines().count());
       
        reader.reset();
        System.out.println("Max line length = "
                + reader.lines().mapToInt(String::length).max().getAsInt());
    }

    private static void collectInOnePass(BufferedReader reader) {
         
        LEStatistics wc = reader.lines().parallel()
                .collect(LEStatistics::new,
                        LEStatistics::accept,
                        LEStatistics::combine);
        System.out.println(wc);
    }

    private static void usage() {
        System.out.println("Usage: " + LE.class.getSimpleName() + " FILE");
        System.out.println("Print something");
    }

    private static class LEStatistics implements Consumer<String> {
        /*
         * @implNote This implementation does not need to be thread safe because
         * the parallel implementation of
         * {@link java.util.stream.Stream#collect Stream.collect()}
         * provides the necessary partitioning and isolation for safe parallel
         * execution.
         */

        private long count1;
        private long count3;
        private long count2;
        private long count4;


        @Override
        public void accept(String line) {
            count1 += line.length();
            count3++;
            count2 += nonWordPattern.splitAsStream(line)
                    .filter(str -> !str.isEmpty()).count();
            count4 = Math.max(count4, line.length());
        }

        public void combine(LEStatistics stat) {
            count2 += stat.count2;
            count3 += stat.count3;
            count1 += stat.count1;
            count4 = Math.max(count4, stat.count4);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("#------LEStatistic------#\n");
            sb.append("count 1 = ").append(count1).append('\n');
            sb.append("count 2 = ").append(count2).append('\n');
            sb.append("count 3 = ").append(count3).append('\n');
            sb.append("count 4 = ").append(count4).append('\n');
            return sb.toString();
        }
    }
}