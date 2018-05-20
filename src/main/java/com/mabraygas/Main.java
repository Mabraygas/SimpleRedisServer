package com.mabraygas;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.databind.JsonNode;
import com.mabraygas.common.CommonResources;
import com.mabraygas.common.Resource;
import com.mabraygas.common.Tools;
import com.mabraygas.common.queue.GlobalWorkerQueue;
import com.mabraygas.common.queue.WorkerQueue;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        CommonResources.Init();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/request", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            m_httpExchange = t;
            String method = t.getRequestMethod();
            String response;
            if (!method.equalsIgnoreCase("post")) {
                response = "Http Method Must Be Post!";
                senderCallBack(response);
            }

            JsonNode node = Tools.JsonParse(t.getRequestBody());

            Integer resourceId = null;
            try {
                resourceId = CommonResources.GetResourceId();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Resource resource = CommonResources.GetResource(resourceId);
            resource.SetCallBack(this::senderCallBack);
            if(!resource.ParseRequest(node)) {
                CommonResources.ReturnResource(resourceId);
            } else {
                if(!resource.IsGlobalRequest()) {
                    WorkerQueue.Push(resource.GetKey(), resourceId);
                } else {
                    GlobalWorkerQueue.Push(0, resourceId);
                }
            }
        }

        public Boolean senderCallBack(String response) {
            try {
                m_httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = m_httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        private HttpExchange m_httpExchange;
    }
}