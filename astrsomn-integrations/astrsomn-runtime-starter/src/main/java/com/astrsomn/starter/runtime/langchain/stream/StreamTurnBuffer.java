package com.astrsomn.starter.runtime.langchain.stream;

import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public final class StreamTurnBuffer {

    private final List<Segment> segments = new ArrayList<>();

    public synchronized void appendThinking(String piece) {
        if (StringUtils.isBlank(piece)) {
            return;
        }
        Segment last = lastOrNull();
        if (last != null && last.kind == Kind.THINKING) {
            last.text.append(piece);
        } else {
            segments.add(Segment.thinking(piece));
        }
    }

    public synchronized void appendText(String piece) {
        if (StringUtils.isBlank(piece)) {
            return;
        }
        Segment last = lastOrNull();
        if (last != null && last.kind == Kind.TEXT) {
            last.text.append(piece);
        } else {
            segments.add(Segment.text(piece));
        }
    }

    public synchronized void addTool(ToolExecution toolExecution) {
        if (toolExecution == null) {
            return;
        }
        segments.add(Segment.tool(toolExecution));
    }

    public synchronized List<Segment> snapshotSegments() {
        return new ArrayList<>(segments);
    }

    private Segment lastOrNull() {
        return segments.isEmpty() ? null : segments.get(segments.size() - 1);
    }

    public enum Kind {
        THINKING,
        TEXT,
        TOOL
    }

    @Getter
    public static final class Segment {
        private final Kind kind;
        private final StringBuilder text;
        private final ToolExecution tool;

        private Segment(Kind kind, StringBuilder text, ToolExecution tool) {
            this.kind = kind;
            this.text = text;
            this.tool = tool;
        }

        static Segment thinking(String first) {
            return new Segment(Kind.THINKING, new StringBuilder(first), null);
        }

        static Segment text(String first) {
            return new Segment(Kind.TEXT, new StringBuilder(first), null);
        }

        static Segment tool(ToolExecution te) {
            return new Segment(Kind.TOOL, new StringBuilder(), te);
        }

        public String textContent() {
            return text != null ? text.toString() : "";
        }
    }
}
