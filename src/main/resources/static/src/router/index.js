import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/product/list'
  },
  {
    path: '/product/list',
    name: 'ProductList',
    component: () => import('../views/ProductList.vue')
  },
  {
    path: '/product/add',
    name: 'ProductAdd',
    component: () => import('../views/ProductAdd.vue')
  },
  {
    path: '/product/edit/:id',
    name: 'ProductEdit',
    component: () => import('../views/ProductEdit.vue')
  },
  {
    path: '/product/detail/:id',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router