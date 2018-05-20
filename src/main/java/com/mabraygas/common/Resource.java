package com.mabraygas.common;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Resource {
    public Resource(int resourceId) {
        m_resourceId = resourceId;
        m_key = "";
        m_value = "";
        m_responseStr = "";
    }

    public void Clear() {
        m_failed = false;
        m_senderCallBack = null;
        m_key = "";
        m_value = "";
        m_responseStr = "";
    }

    public int m_resourceId;
    public Boolean m_failed;
    public Commands m_commands;
    public String m_responseStr;

    public String m_key;
    public String m_value;
    public long m_expire;

    private Function<String, Boolean> m_senderCallBack;

    public void SetCallBack(Function<String, Boolean> cb) {
        m_senderCallBack = cb;
    }

    public Boolean InvokeCB() {
        if(m_responseStr.length() > 0) {
            return m_senderCallBack.apply(m_responseStr);
        }

        return m_senderCallBack.apply("callback in sender.");
    }

    public Boolean ParseRequest(JsonNode jsonNode) {
        JsonNode commandNode = jsonNode.findValue("Commands");
        String command = ProcessJsonNodeToString(commandNode);
        if (command == null) {
            return false;
        }
        switch(command) {
            case "Exists": {
                m_commands = Commands.EXISTS;
                break;
            }
            case "Del": {
                m_commands = Commands.DEL;
                break;
            }
            case "Expire": {
                m_commands = Commands.EXPIRE;
                break;
            }
            case "TTL": {
                m_commands = Commands.TTL;
                break;
            }
            case "Get": {
                m_commands = Commands.GET;
                break;
            }
            case "Set": {
                m_commands = Commands.SET;
                break;
            }
            default:
                m_commands = Commands.OTHERS;
        }
        if (m_commands == Commands.OTHERS) {
            return false;
        }

        JsonNode keyNode = jsonNode.findValue("Key");
        JsonNode valueNode = jsonNode.findValue("Value");
        JsonNode expireNode = jsonNode.findValue("Expire");

        m_key = ProcessJsonNodeToString(keyNode);
        m_value = ProcessJsonNodeToString(valueNode);
        Integer expire = ProcessJsonNodeToInt(expireNode);
        m_expire = expire != null ? Integer.toUnsignedLong(expire) : 0;

        return true;
    }

    public Boolean IsGlobalRequest() {
        return m_commands.IsGlobalRequest();
    }

    public String GetKey() {
        return m_key.length() == 0 ? null : m_key;
    }

    private String ProcessJsonNodeToString(JsonNode node) {
        return node == null ? null : node.textValue();
    }

    private Integer ProcessJsonNodeToInt(JsonNode node) {
        return node == null ? null : node.asInt();
    }

    private void ProcessJsonNodeList(List<JsonNode> list, ArrayList<String> processedList) {
        for(JsonNode node : list) {
            if(node != null) {
                String str = node.textValue();
                if(str != null) {
                    processedList.add(str);
                }
            }
        }
    }
}
