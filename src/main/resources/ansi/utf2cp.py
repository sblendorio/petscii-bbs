#!/usr/bin/env python
import codecs, sys
try:
    infile, outfile = sys.argv[1], sys.argv[2]
except IndexError:
    sys.stderr.write('usage: %s input_file output_file\n' % sys.argv[0])
    sys.exit(1)
nfo = codecs.open(infile, encoding='utf-8').read()
codecs.open(outfile, 'w', encoding='cp437').write(nfo)
