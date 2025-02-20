FROM node:18-alpine

# set working directory
WORKDIR /app

# copy package.json and package-lock.json
COPY package*.json ./

# install dependencies
RUN npm install

# copy frontend code
COPY . .

# build w/ parcel
RUN npm run build

EXPOSE 1234

# start app
CMD ["npm", "start"]